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
import com.example.ikn.repository.Repository
import com.example.ikn.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val repo: Repository, private val sharedPrefManager: SharedPreferencesManager?) : ViewModel() {

    private val _authorized : MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val authorized : LiveData<Boolean> = _authorized

    fun loginHandler(email: String, password: String) = viewModelScope.launch {
        val res: Response<LoginResponse> = repo.postLogin(email, password)

        if (res.isSuccessful) {
            sharedPrefManager?.setString("TOKEN", res.body()?.token)
            _authorized.value = true
            Log.i("[VM: LOGIN]" ,"${res.body()?.token}")
        } else {
            _authorized.value = false
            Log.i("[VM: LOGIN]" ,"${res.code()}")
        }
    }



}