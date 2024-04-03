package com.example.ikn.repository

import com.example.ikn.utils.SharedPreferencesManager
import java.io.IOException

class PreferenceRepository (private val prefManager: SharedPreferencesManager) {
    fun isKeepLoggedIn() : Boolean {
        val status = prefManager.get(keepKey) ?: return false
        return status.toBoolean()
    }
    fun setKeepLoggedIn(status: Boolean) {
        prefManager.set(keepKey, status.toString())
    }
    fun saveToken(tokenValue: String) {
        prefManager.set(tokenKey, tokenValue)
    }
    fun clearToken() {
        prefManager.set(tokenKey, null)
    }
    fun getToken(): String {
        return prefManager.get(tokenKey) ?: return ""
    }
    fun setSignInInfo(emailVal: String, passwordVal: String) {
        prefManager.set(emailKey, emailVal);
        prefManager.set(passwordKey, passwordVal);
    }
    fun getSignInInfo() : Pair<String, String> {
        val email = prefManager.get(emailKey)
        val password = prefManager.get(passwordKey)

        if (email == null || password == null) return Pair("", "")

        return Pair(email, password)
    }
    companion object {
        const val keepKey = "IS_KEEP_VAL"
        const val tokenKey = "TOKEN_VAL"
        const val emailKey = "EMAIL_INFO"
        const val passwordKey = "PASSWORD_INFO"

        @Volatile
        private var instance: PreferenceRepository? = null
        fun getInstance(preference: SharedPreferencesManager): PreferenceRepository {
            return instance ?: synchronized(this) {
                instance ?: PreferenceRepository(preference).also { instance = it }
            }
        }
    }
}