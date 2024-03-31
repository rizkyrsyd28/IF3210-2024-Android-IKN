package com.example.ikn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ikn.data.AppDatabase
import com.example.ikn.databinding.ActivityMainBinding
import java.util.Locale
import com.example.ikn.utils.SharedPreferencesManager

class MainActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database
        appDatabase = AppDatabase.getInstance(this.applicationContext)

        supportActionBar?.hide()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.text = destination.label
                ?.split("_")
                ?.findLast { _ ->
                    true
                }
                ?.replaceFirstChar { it.uppercase() }
        }

        val sharedPref = SharedPreferencesManager(this)
        val token = sharedPref.get("TOKEN")

        Log.i("MAIN", "Data - $token")

    }
}