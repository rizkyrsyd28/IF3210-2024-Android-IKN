package com.example.ikn.ui.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ikn.R
import com.example.ikn.service.network.NetworkService
import com.example.ikn.service.token.TokenService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        Log.i(TAG, "Start Create")

        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, LoginFragment.newInstance())
                .commit()
        }
        Log.i(TAG, "Finish Create")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "Start Start")
    }

    override fun onResume() {
        super.onResume()
        Log.w(TAG, "Start Resume")
        val service = (Intent(this, NetworkService::class.java))
        val tokenService = (Intent(this, TokenService::class.java))
        startService(service)
        startService(tokenService)
        Log.w(TAG, "Finish Resume")
    }

    override fun onPause() {
        super.onPause()
        stopService(Intent(this, TokenService::class.java))
        stopService(Intent(this, NetworkService::class.java))

    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Destroy LoginActivity config change $isChangingConfigurations, , finsih - $isFinishing")
    }

    companion object {
        const val TAG = "[LOGIN ACTIVITY]"
    }
}