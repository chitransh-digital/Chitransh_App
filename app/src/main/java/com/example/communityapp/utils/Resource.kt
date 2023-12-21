package com.example.communityapp.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: Exception) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
}