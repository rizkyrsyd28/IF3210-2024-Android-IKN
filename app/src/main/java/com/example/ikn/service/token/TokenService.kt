package com.example.ikn.service.token

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.ikn.model.response.LoginResponse
import com.example.ikn.model.response.TokenResponse
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class TokenService : Service() {
    private val repo: Repository = Repository()
    private lateinit var prefRepo: PreferenceRepository
    private lateinit var job: Job
    /* State */
    private var isKeepLoggedIn: Boolean = false

    override fun onCreate() {
        super.onCreate()
        prefRepo = PreferenceRepository(SharedPreferencesManager(applicationContext))
        isKeepLoggedIn = prefRepo.isKeepLoggedIn()

        setupTokenJobSchedule()

        Log.d(TAG, "Token Service created - $isKeepLoggedIn")
    }
    private fun setupTokenJobSchedule() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                isKeepLoggedIn = prefRepo.isKeepLoggedIn()

                if (isTokenExpired()) {
                    if (isKeepLoggedIn) {
                        restartToken()
                    } else {
                        forceLogOut()
                    }
                }
                delay(period)
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
    private suspend fun restartToken() {
        try {
            val signInInfo = prefRepo.getSignInInfo()
            val res: Response<LoginResponse> = repo.postLogin(signInInfo.first, signInInfo.second)

            if (!res.isSuccessful || res.body() == null) throw Exception("Failed LogIn")

            prefRepo.saveToken(res.body()!!.token)
            Log.i(TAG, "Refresh Token Success")

        } catch (exp: Exception) {
            forceLogOut()
            Log.e(TAG, "Exception: ${exp.message} ")
        }
    }
    private fun forceLogOut() {
        prefRepo.clearToken()
        prefRepo.setSignInInfo("","")
        prefRepo.setKeepLoggedIn(false)
        signOutBroadcast()
    }
    private suspend fun isTokenExpired() : Boolean {

        val res: Response<TokenResponse> = repo.postToken(prefRepo.getToken())

        if (!res.isSuccessful) {
            Log.e(TAG, "Unsuccessful")
            return false
        }

        if (res.body() == null) {
            Log.e(TAG, "Cant Get Token")
            return false
        }
        val exp = res.body()!!.exp
        val iat = res.body()!!.iat

        return exp - iat < 45
    }
    private fun signOutBroadcast() {
        val intent = Intent("TOKEN_LOGOUT").apply {}
        sendBroadcast(intent)
    }
    companion object {
        const val TAG = "[TOKEN SERVICE]"
        var period = TimeUnit.MINUTES.toMillis(4) + TimeUnit.SECONDS.toMillis(20)
    }
}