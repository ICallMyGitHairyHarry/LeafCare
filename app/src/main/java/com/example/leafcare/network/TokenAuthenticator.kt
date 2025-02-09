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

/*
// Improvement: Differentiate Between Network Errors and Auth Errors
//No, the Authenticator itself cannot directly modify the HTTP response code. It works at the network layer and its job is to:
//
//Attempt to refresh the token.
//Retry the request with the new token if successful.
//If the token refresh fails:
//
//The original response (401 Unauthorized from the server) is passed back to your app logic.
//The Authenticator cannot indicate network errors directly to your app.

// authenticator
override fun authenticate(route: Route?, response: Response): Request? {
    return try {
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        val tokenResponse = apiService.refreshToken(refreshToken).execute()

        if (tokenResponse.isSuccessful) {
            val body = tokenResponse.body() ?: throw Exception("Empty response body")
            tokenManager.saveAccessToken(body.accessToken)
            tokenManager.saveRefreshToken(body.refreshToken)

            // Retry the original request with the new token
            response.request.newBuilder()
                .header("Authorization", "Bearer ${body.accessToken}")
                .build()
        } else {
            // Handle specific server errors (e.g., invalid refresh token)
            throw AuthenticatorException("Auth error: ${tokenResponse.code()}")
        }
    } catch (e: IOException) {
        // Network error
        throw NetworkErrorException("Network error: ${e.message}", e)
    } catch (e: Exception) {
        // Other errors
        throw AuthenticatorException("Authentication error: ${e.message}", e)
    }
}

// Custom Exceptions:
class AuthenticatorException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NetworkErrorException(message: String, cause: Throwable? = null) : IOException(message, cause)

// App-Level Error Handling (e.g. ViewModel):
try {
    val response = apiService.getProtectedResource()
    // Handle the successful response
} catch (e: NetworkErrorException) {
    // Handle network issues
    showError("Network error occurred. Please try again.")
} catch (e: AuthenticatorException) {
    // Handle authentication issues
    logOutUser()
    showError("Session expired. Please log in again.")
} catch (e: Exception) {
    // Handle other errors
    showError("An unexpected error occurred.")
}
*/


