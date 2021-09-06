package org.techtown.wishmatching

import android.content.Context
import android.content.SharedPreferences

class PreferenceProfile(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    fun getString(key: String, value: String): String? {
        return prefs.getString(key, value)
    }
    fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    } }
