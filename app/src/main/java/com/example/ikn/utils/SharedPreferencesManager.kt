package com.example.ikn.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager (context: Context) {
    companion object {
        const val NAME_PREF = "IKN_PREF"

        @Volatile private var instance: SharedPreferencesManager? = null // Volatile modifier is necessary

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
    }

    private val sharedPreferences : SharedPreferences
    private val editor : SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun get(key: String) : String? {
        return sharedPreferences.getString(key, null)
    }

    fun set(key:String, value: String?) {
        editor.putString(key, value).apply()
    }
}