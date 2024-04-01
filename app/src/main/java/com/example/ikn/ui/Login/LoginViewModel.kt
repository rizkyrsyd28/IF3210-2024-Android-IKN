package com.example.ikn.ui.Login

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ikn.model.response.LoginResponse
import com.example.ikn.model.response.TokenResponse
import com.example.ikn.repository.PreferenceRepository
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class LoginViewModel(private val repo: Repository, private val prefRepo: PreferenceRepository) : ViewModel() {

    private val _authorized : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val authorized : LiveData<Boolean> = _authorized

    private val _failed : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val failed : LiveData<Boolean> = _failed
    var failedMessage = ""

    fun signInHandler(email: String, password: String, isKeepLoggedIn: Boolean) = viewModelScope.launch {
        try {
            val res: Response<LoginResponse> = repo.postLogin(email, password)

            if (!res.isSuccessful && res.body() == null) {
                _authorized.value = false
                _failed.value = true
                failedMessage = "Failed To SignIn, Check Your Email or Password"
                return@launch
            }

            _authorized.value = true
            prefRepo.setSignInInfo(email, password)
            prefRepo.saveToken(res.body()!!.token)
            prefRepo.setKeepLoggedIn(isKeepLoggedIn)

            if (isKeepLoggedIn) prefRepo.setSignInInfo(email, password)
        } catch (exp: SocketTimeoutException) {
            _failed.value = true
            failedMessage = "Bad Connection"
            Log.e("[VIEW MODEL LOGIN]", "Error - ${exp.message}")
        }
    }
    fun isAllowKeepLoggedIn(): Boolean {
        val isKeepLoggedIn = prefRepo.isKeepLoggedIn()
        val signInInfo = prefRepo.getSignInInfo()

        if (!isKeepLoggedIn) return false
        if (signInInfo.first == "" && signInInfo.second == "") return false

        var result = false
        runBlocking {
            val resToken: Response<TokenResponse> = repo.postToken(prefRepo.getToken())
            result = resToken.isSuccessful
            Log.w("[VIEW MODEL LOGIN]", "Hasil IN SCOPE - $result")
        }

        Log.w("[VIEW MODEL LOGIN]", "Hasil - $result")
        return result
    }

}