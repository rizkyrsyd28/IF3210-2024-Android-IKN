package com.example.ikn.service.token

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TokenService : Service() {
    private val repo: Repository = Repository()
    private val pref: SharedPreferencesManager = SharedPreferencesManager(applicationContext)
    private lateinit var job: Job
    override fun onCreate() {
        super.onCreate()
        tokenRefresh()
        Log.d(TAG, "Token Service created")
    }
    private fun tokenRefresh() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                repo.postLogin()

                delay(TimeUnit.MINUTES.toMillis(4))
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "Token Service created")

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG = "[TOKEN SERVICE]"
    }
}