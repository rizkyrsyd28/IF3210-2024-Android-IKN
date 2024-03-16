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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        supportActionBar?.hide()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

//        val menu = binding.bottomNavigationView.menu
//
//        for (i in 0 until menu.size()) {
//            val menuItem = menu.getItem(i)
//            val actionView = LayoutInflater.from(this)
//                .inflate(R.layout.bottom_navigation_item, null)
//
//            val icon = actionView.findViewById<ImageView>(R.id.icon)
//            val title = actionView.findViewById<TextView>(R.id.title)
//
//            icon.setImageDrawable(menuItem.icon)
//            title.text = menuItem.title
//
//            menuItem.actionView = actionView
//        }

//        setupActionBarWithNavController(navController, AppBarConfiguration(
//            setOf(
//                R.id.nav_scan,
//                R.id.nav_transaction,
//                R.id.nav_graf,
//                R.id.nav_setting
//            )
//        )
//        )

    }
}