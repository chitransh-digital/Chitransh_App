package com.example.communityapp.data.models

import java.io.Serializable

data class Business(
    val name : String,
    val ownerID : String,
    val contact : String,
    val desc : String,
    val address : String,
    val type:String,
    val link:String,
    val images: List<String>,
    val coupon:String
):Serializable
