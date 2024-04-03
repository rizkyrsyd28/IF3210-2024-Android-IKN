package com.example.ikn.service.file

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class FileBroadcastReceiver(
    private val openHandler : (String) -> Unit,
    private val sendEmailHandler : (String, String) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i(TAG, "intent ${intent.action}")
        if (intent.action == null || intent.extras == null) return

        when (intent.action) {
            "com.example.ikn.OPEN_FILE" -> {
                val path = intent.extras!!.getString("DIR")!!
                openHandler(path)
            }
            "com.example.ikn.OPEN_EMAIL" -> {
                val path = intent.extras!!.getString("DIR")!!
                val sendTo = intent.extras!!.getString("SEND_TO")!!
                sendEmailHandler(path, sendTo)
            }
        }
    }

    companion object {
        const val TAG = "[FILE BROADCAST]"
    }
}