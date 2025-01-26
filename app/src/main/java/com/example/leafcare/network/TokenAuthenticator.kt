package com.example.leafcare.network

import com.example.leafcare.data.model.RefreshTokenRequest
import com.example.leafcare.data.storage.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator (
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            // Check if the request has already been retried
            if (response.request.header("Authorization") == null) {
                return null
            }

            // Get the current refresh token
            val refreshToken = tokenManager.getRefreshToken()

            // Refresh the token using the API
            val newAccessToken = try {
                // refreshToken can't be null because there is check in LauncherActivity
                val tokenResponse = AuthApi.retrofitService.refreshTokens(RefreshTokenRequest(refreshToken!!)).execute()
                if (tokenResponse.isSuccessful) {
                    val body = tokenResponse.body()
                    if (body != null) {
                        // Save both tokens
                        tokenManager.saveTokens(body.accessToken, body.refreshToken)
                        body.accessToken // Return the accessToken for retry logic
                    } else {
                        null // Handle empty response body
                    }
                } else {
                    null // Refresh failed
                }
            } catch (e: Exception) {
                null // Handle errors during token refresh
            }

            // If the token is null, return null (this will end the retry attempt)
            if (newAccessToken.isNullOrEmpty()) {
                return null
            }

            // Retry the failed request with the new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }
}



