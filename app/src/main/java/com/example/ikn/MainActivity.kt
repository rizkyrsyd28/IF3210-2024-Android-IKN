package com.example.ikn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ikn.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController


        binding.bottomNavigationView.setupWithNavController(navController)

        val toolbar = binding.toolbar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = destination.label
                ?.split("_")
                ?.findLast { _ ->
                    true
                }
                ?.replaceFirstChar { it.uppercase() }
        }

//        setupActionBarWithNavController(navController, AppBarConfiguration(
//            setOf(
//                R.id.nav_scan,
//                R.id.nav_transaction,
//                R.id.nav_graf,
//            )
//        )
//        )

    }
}