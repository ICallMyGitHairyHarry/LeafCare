package com.example.leafcare.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leafcare.data.model.ApiStatus
import com.example.leafcare.data.model.LoginRequest
import com.example.leafcare.data.model.RegisterRequest
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.network.AuthApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // network status value for auth fragment
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus> = _status

    // error message
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


//    init {
//    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                val response = AuthApi.retrofitService
                    .login(LoginRequest(email, password))
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                tokenManager.saveEmail(email)
                _status.value = ApiStatus.DONE
                _error.value = ""
            } catch (e: Exception) {
                setErrorFromRequest(e)
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                val response = AuthApi.retrofitService
                    .register(RegisterRequest(name, email, password))
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
                tokenManager.saveEmail(email)
                _status.value = ApiStatus.DONE
                _error.value = ""
            } catch (e: Exception) {
                setErrorFromRequest(e)
                _status.value = ApiStatus.ERROR
            }
        }
    }

    private fun setErrorFromRequest (e: Exception) {
        when (e) {
            is HttpException -> {
                // Handle HTTP exceptions (like 422, 401, etc.)
                val errorCode = e.code()
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(
                    "HTTP Error",
                    "Code: $errorCode, Body: ${errorBody ?: "Unknown error"}"
                )

                // Handle specific HTTP error codes if necessary
                when (errorCode) {
                    400 -> _error.value = "Пользователь с таким email или именем уже существует"
                    401 -> _error.value = "Неверный логин или пароль"
                    422 -> _error.value = "Email должен быть в формате example@mail.com"
                    else -> _error.value = "Неизвестная ошибка сервера"
                }
            }

            // Handle timeout exceptions
            is java.net.SocketTimeoutException -> {
                Log.e(
                    "Timeout Error",
                    e.toString()
                )
                _error.value = "Ошибка соединения, сервер не отвечает"
            }

            // Handle no internet connection or bad URL
            is java.net.UnknownHostException -> {
                Log.e(
                    "Unknown Host Error",
                    e.toString()
                )
                _error.value = "Нет соединения или сервер недоступен"
            }

            // Handle generic I/O exceptions (e.g., no connection)
            is IOException -> {
                Log.e(
                    "IO Error",
                    e.toString()
                )
                _error.value = "Ошибка соединения"
            }

            // Handle any other exceptions
            else -> {
                Log.e(
                    "Other Error",
                    e.toString()
                )
                _error.value = "Неизвестная ошибка"
            }

        }
    }

    fun clearErrorOnNav () {
        _error.value = ""
    }


}