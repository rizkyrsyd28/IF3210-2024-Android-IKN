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
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.io.IOException
import java.time.Instant
import java.util.concurrent.TimeUnit

class TokenService : Service() {
    private val repo: Repository = Repository()
    private lateinit var prefRepo: PreferenceRepository
    private lateinit var job: Job
    /* State */
    private var isKeepLoggedIn: Boolean = false
    private var period = TimeUnit.MINUTES.toMillis(5)

    override fun onCreate() {
        super.onCreate()
        prefRepo = PreferenceRepository(SharedPreferencesManager(applicationContext))
        prefRepo.setKeepLoggedIn(false)
        isKeepLoggedIn = prefRepo.isKeepLoggedIn()

        if (isKeepLoggedIn) {
            runBlocking {
                val signInInfo = prefRepo.getSignInInfo()
                val res: Response<LoginResponse> = repo.postLogin(signInInfo.first, signInInfo.second)
                if (res.isSuccessful) prefRepo.saveToken(res.body()?.token!!)
                Log.w(TAG, "Hasil - ${res.isSuccessful}")
            }
        }

        setupTokenJobSchedule()

        Log.d(TAG, "Token Service created - $period")
    }
    private fun setupTokenJobSchedule() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                isKeepLoggedIn = prefRepo.isKeepLoggedIn()
                Log.i(TAG, "Period Data")

                delay(period)

                if (isTokenExpired()) {
                    Log.i(TAG, "Expired Token")
                    if (isKeepLoggedIn) {
                        Log.i(TAG, "Keep Login Token")
                        restartToken()
                    } else {
                        Log.i(TAG, "Not Keep Login Token")
                        forceLogOut()
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "Token Service destroyed")

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

            val resToken: Response<TokenResponse> = repo.postToken(prefRepo.getToken())

            if (!res.isSuccessful || res.body() == null) throw Exception("Failed Get Token")
            val exp = resToken.body()!!.exp
            val now = Instant.now().epochSecond

            period = (exp - now) * 1000
        } catch (exp: Exception) {
            forceLogOut()
            Log.e(TAG, "Exception: ${exp.message} ")
        }
    }
    private fun forceLogOut() {
        Log.i(TAG, "Try To Force LogOut")
        prefRepo.clearToken()
        prefRepo.setSignInInfo("","")
        prefRepo.setKeepLoggedIn(false)
        signOutBroadcast()
    }
    private suspend fun isTokenExpired() : Boolean {

        val res: Response<TokenResponse> = repo.postToken(prefRepo.getToken())

        Log.e(TAG, "Result - issuc ${res.isSuccessful} - code ${res.code()}")

        if (!res.isSuccessful || res.body() == null) {
            Log.e(TAG, "Cant Get Token")
            return true
        }
        val exp = res.body()!!.exp
        val now = Instant.now().epochSecond

        Log.e(TAG, "Difference ${exp - now}")
        return exp - now < 60
    }
    private fun signOutBroadcast() {
        val intent = Intent("TOKEN_LOGOUT").apply {}
        sendBroadcast(intent)
    }
    companion object {
        const val TAG = "[TOKEN SERVICE]"
    }
}