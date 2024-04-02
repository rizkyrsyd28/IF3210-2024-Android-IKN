package com.example.ikn.service.token

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TokenBroadcastReceiver(
    private val forceLogOutHandler :  () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "intent = ${intent.action}")
        if (intent.action != ACTION) return
         forceLogOutHandler()
    }

    companion object {
        const val TAG = "[TOKEN BROADCAST]"
        const val ACTION = "TOKEN_LOGOUT"
    }
}