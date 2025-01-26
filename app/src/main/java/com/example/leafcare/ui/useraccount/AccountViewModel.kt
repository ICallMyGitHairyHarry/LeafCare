package com.example.leafcare.ui.useraccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafcare.data.storage.TokenManager

class AccountViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // user email
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    init {
        _email.value = tokenManager.getEmail()
    }

    fun clearAuthTokens() {
        tokenManager.clearTokens()
    }

}