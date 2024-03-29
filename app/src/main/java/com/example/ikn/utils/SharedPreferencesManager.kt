package com.example.ikn.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager (context: Context) {
    private val NAME_PREF = "BM_PREF"
    private val sharedPreferences : SharedPreferences
    private val editor : SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(NAME_PREF, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun getString(key: String) : String? {
        return sharedPreferences.getString(key, null)
    }

    fun setString(key:String, value: String?) {
        editor.putString(key, value).apply()
    }
}