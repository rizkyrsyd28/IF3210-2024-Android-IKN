package com.example.ikn.service.network

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log

class NetworkManager(private val connectivityManager: ConnectivityManager) {

    private val networkRequest : NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private var networkCallback: NetworkCallback = object : NetworkCallback() {}

    fun setupNetwork(
        connectedBroadcast: () -> Unit,
        disconnectedBroadcast: () -> Unit
    ) {
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d(TAG, "Network Available")
                connectedBroadcast();
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d(TAG, "Network Lost")
                disconnectedBroadcast();
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun finish() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
    fun checkNetworkSync(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private val TAG = "[NETWORK MANAGER]"
}