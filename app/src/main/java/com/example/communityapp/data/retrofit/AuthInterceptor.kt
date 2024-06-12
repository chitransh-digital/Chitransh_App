package com.example.communityapp.data.retrofit

import android.content.Context
import com.example.communityapp.data.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Retrieve the token from preferences
        val token = preferences.getToken()

        // Add the authorization header to the original request
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
