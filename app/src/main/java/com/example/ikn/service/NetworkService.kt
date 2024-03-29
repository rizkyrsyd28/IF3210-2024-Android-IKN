package com.example.ikn.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


class NetworkService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.d("[SERVICE]", "Network Service created")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("[SERVICE]", "Network Service Destroyed")
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}