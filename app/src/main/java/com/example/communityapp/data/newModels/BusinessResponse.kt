package com.example.communityapp.data.newModels

data class BusinessResponse(
    var businesses: List<Business>,
    var count: Int,
    var message: String,
    var status: Boolean
)