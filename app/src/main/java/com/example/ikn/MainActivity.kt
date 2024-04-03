package com.example.ikn

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ikn.data.AppDatabase
import com.example.ikn.databinding.ActivityMainBinding
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.service.network.NetworkBroadcastReceiver
import com.example.ikn.service.network.NetworkService
import com.example.ikn.service.token.TokenBroadcastReceiver
import com.example.ikn.service.token.TokenService
import com.example.ikn.utils.SharedPreferencesManager

class MainActivity : AppCompatActivity() {

    private lateinit var appDatabase: AppDatabase
    private lateinit var tokenReceiver: TokenBroadcastReceiver
    private lateinit var networkReceiver: NetworkBroadcastReceiver

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the database
        appDatabase = AppDatabase.getInstance(this.applicationContext)

        supportActionBar?.hide()

        tokenReceiver = TokenBroadcastReceiver(forceLogOutHandler = { signOutHandler() })
        registerReceiver(tokenReceiver, IntentFilter("TOKEN_LOGOUT"))

        networkReceiver = NetworkBroadcastReceiver()
        registerReceiver(networkReceiver, IntentFilter("NETWORK_STATUS"))

        networkReceiver.setConnectedHandler { Log.e(TAG, "connected") }
        networkReceiver.setDisconnectedHandler { Log.e(TAG, "disconnected") }

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
    override fun onResume() {
        super.onResume()
        val service = (Intent(this, NetworkService::class.java))
        val tokenService = (Intent(this, TokenService::class.java))
        startService(service)
        startService(tokenService)
    }
    private fun signOutHandler() {
        Log.e("[MAIN]", "Main SignOut method Run ")

        val sharedPref = SharedPreferencesManager(this)
        val prefRepo = PreferenceRepository(sharedPref)

        prefRepo.clearToken()
        prefRepo.setSignInInfo("", "")
        prefRepo.setKeepLoggedIn(false)

        startActivity(Intent(this@MainActivity, SplashActivity::class.java))
        finish()
    }

    override fun onPause() {
        super.onPause()
        stopService(Intent(this, TokenService::class.java))
        stopService(Intent(this, NetworkService::class.java))
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(tokenReceiver)
        unregisterReceiver(networkReceiver)
        Log.w("[MAIN]", "Main Destroy Config - $isChangingConfigurations, finsih - $isFinishing")
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT, ).show()
        }
    }

    companion object {
        const val TAG = "[MAIN]"
    }
}