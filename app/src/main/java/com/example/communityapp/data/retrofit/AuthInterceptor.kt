package com.example.communityapp.data.retrofit

import android.content.Context
import com.example.communityapp.data.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val context: Context, val preferences: PreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Retrieve the token from preferences
        val token = preferences.getToken()

        // List of paths to exclude from adding the Cookie header
        val excludePaths = listOf("/api/user/loginPhone", "/api/user/loginPhoneFamilyID")

        // Add the Cookie header if the path is not in the exclude list
        val newRequestBuilder = originalRequest.newBuilder()
        if (!excludePaths.contains(originalUrl.encodedPath)) {
            newRequestBuilder.addHeader("Cookie", "jwt=$token")
        }

        val newRequest = newRequestBuilder.build()
        return chain.proceed(newRequest)
    }
}
