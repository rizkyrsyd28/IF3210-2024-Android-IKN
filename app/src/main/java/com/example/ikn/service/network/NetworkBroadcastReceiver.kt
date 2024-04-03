package com.example.ikn.service.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NetworkBroadcastReceiver : BroadcastReceiver() {
    private var connectedHandler: () -> Unit = {
        Log.w(TAG, "this is default message from connected Handler")
    }
    private var disconnectedHandler: () -> Unit = {
        Log.w(TAG, "this is default message from disconnected Handler")
    }

    fun setConnectedHandler(handler: () -> Unit) {
        connectedHandler = handler
    }
    fun setDisconnectedHandler(handler: () -> Unit) {
        disconnectedHandler = handler
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION && intent.extras?.isEmpty!!) return

        val status = intent.extras?.getBoolean(EXTRAS_KEY)!!

        Log.w(TAG, "Broadcast Network Receive Network Status = $status")

        if (status) connectedHandler() else disconnectedHandler()
    }

    companion object {
        const val TAG = "[NETWORK BROADCAST]"
        const val ACTION = "NETWORK_STATUS"
        const val EXTRAS_KEY = "status"
    }
}