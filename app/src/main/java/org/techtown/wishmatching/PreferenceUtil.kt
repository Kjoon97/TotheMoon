package org.techtown.wishmatching

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)
    fun getString(key: String, flag: Int): Int {
        return prefs.getInt(key,flag)
    }
    fun setString(key: String, flag: Int) {
        prefs.edit().putInt(key, flag).apply()
    } }
