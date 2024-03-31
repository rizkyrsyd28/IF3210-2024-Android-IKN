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

class LoginViewModel(private val repo: Repository, private val prefRepo: PreferenceRepository) : ViewModel() {

    private val _authorized : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val authorized : LiveData<Boolean> = _authorized

    fun signInHandler(email: String, password: String, isKeepLoggedIn: Boolean) = viewModelScope.launch {
        val res: Response<LoginResponse> = repo.postLogin(email, password)

        if (!res.isSuccessful && res.body() == null) {
            _authorized.value = false
            throw Exception("LogIn Failed")
        }

        _authorized.value = true
        prefRepo.saveToken(res.body()!!.token)
        prefRepo.setKeepLoggedIn(isKeepLoggedIn)

        if (isKeepLoggedIn) prefRepo.setSignInInfo(email, password)
    }
    fun isAllowKeepLoggedIn(): Boolean {
        val isKeepLoggedIn = prefRepo.isKeepLoggedIn()
        val signInInfo = prefRepo.getSignInInfo()

        if (!isKeepLoggedIn && (signInInfo.first == "" && signInInfo.second == "")) return false

        var result = false
        runBlocking {
            val resToken: Response<TokenResponse> = repo.postToken(prefRepo.getToken())
            result = resToken.isSuccessful
            Log.w("[VIEW MODEL LOGIN]", "Hasil IN SCOPE - $result")
        }

        Log.w("[VIEW MODEL LOGIN]", "Hasil - $result")
        return result
    }

//    fun isAllowKeepLoggedIn(): Boolean {
//        val isKeepLoggedIn = prefRepo.isKeepLoggedIn()
//        val signInInfo = prefRepo.getSignInInfo()
//
//        if (!isKeepLoggedIn) return false
//        return !(signInInfo.first == "" && signInInfo.second == "")
//    }
}