package com.example.ikn.ui.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ikn.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, LoginFragment.newInstance())
                .commit()
        }
    }
}