package com.example.ikn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.ikn.service.network.NetworkService
import com.example.ikn.service.token.TokenService
import com.example.ikn.ui.Login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        , 3000)
    }

    override fun onResume() {
        super.onResume()
        val tokenService = (Intent(this, TokenService::class.java))
        startService(tokenService)
    }

    override fun onPause() {
        super.onPause()
        stopService(Intent(this, TokenService::class.java))
        Log.w("[SPLASH]", "Splash Stop Config - $isChangingConfigurations, finsih - $isFinishing")

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.w("[SPLASH]", "Splash Destroy Config - $isChangingConfigurations, finsih - $isFinishing")
    }
}