package com.example.ikn.ui.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ikn.R
import com.example.ikn.service.network.NetworkService

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        Log.i("[LOGIN]", "Create LoginActivity")

        setContentView(R.layout.activity_login)

        val service = (Intent(this, NetworkService::class.java))
        startService(service)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, LoginFragment.newInstance())
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("[LOGIN]", "Start LoginActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("[LOGIN]", "Destroy LoginActivity")
    }
}