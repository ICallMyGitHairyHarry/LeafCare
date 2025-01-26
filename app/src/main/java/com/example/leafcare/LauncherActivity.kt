package com.example.leafcare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.leafcare.data.storage.TokenManager

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable splash screen transition
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launcher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check token logic
        val tokenManager = TokenManager(applicationContext)
        val accessToken = tokenManager.getAccessToken()
//        Log.d("LAUNCH", accessToken.toString())

        if (accessToken.isNullOrEmpty()) {
            startActivity(Intent(this, AuthActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Close the launcher activity
        finish()
    }
}