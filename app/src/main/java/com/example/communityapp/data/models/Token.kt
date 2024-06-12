package com.example.communityapp.data.models

import java.io.Serializable

// Request body data class
data class LoginRequest(
    val phone: String
):Serializable

data class LoginRequestByID(
    val phone: String,
    val familyID: String
):Serializable

// Response body data class
data class LoginResponse(
    val token: String
):Serializable

