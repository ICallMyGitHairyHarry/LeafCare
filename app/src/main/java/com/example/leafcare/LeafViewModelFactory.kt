package com.example.leafcare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafcare.data.storage.TokenManager
import com.example.leafcare.ui.auth.AuthViewModel
import com.example.leafcare.ui.dashboard.DashboardViewModel
import com.example.leafcare.ui.home.HomeViewModel
import com.example.leafcare.ui.useraccount.AccountViewModel

class LeafViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(tokenManager) as T
        } else if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}