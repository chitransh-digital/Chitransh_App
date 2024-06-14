package com.example.communityapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.communityapp.utils.Constants
import javax.inject.Inject

class PreferencesHelper @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, Constants.KEY_AUTH_TOKEN)
    }

    fun setToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getContact(): String? {
        return sharedPreferences.getString(CONTACT, null)
    }

    fun setContact(token: String) {
        sharedPreferences.edit().putString(CONTACT, token).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }



    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_AUTH_TOKEN = Constants.KEY_AUTH_TOKEN
        private const val CONTACT = "user_number"
    }
}
