package com.example.ikn.service.network

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkService : Service() {
    private lateinit var networkManager: NetworkManager
    override fun onCreate() {
        super.onCreate()
        networkManager = NetworkManager(getSystemService(ConnectivityManager::class.java))
        startNetworkMonitor();

        Log.d(TAG, "Network Service created")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isConnected = networkManager.checkNetworkSync()
        Log.w("[ON START SERVICE]", "On Start - $isConnected")
        sendNetworkStatus(isConnected)
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            networkManager.finish()
        } catch (err: Exception) {
            Log.e("[ERROR]", "Error - $err")
        }

        Log.d(TAG, "Network Service Destroyed")
    }
    private fun startNetworkMonitor() {
        CoroutineScope(Dispatchers.IO).launch {
            networkManager.setupNetwork(
                connectedBroadcast = { sendNetworkStatus(true) },
                disconnectedBroadcast = { sendNetworkStatus(false) }
            )
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun sendNetworkStatus(isConnected: Boolean) {
        val intent = Intent("NETWORK_STATUS").apply {
            putExtra("status", isConnected)
        }
        sendBroadcast(intent)
    }

    companion object {
        const val TAG = "[NETWORK SERVICE]"
    }
}