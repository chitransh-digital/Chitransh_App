package com.example.communityapp.data.newModels

import java.io.Serializable

data class Business(
    var attachments: List<String>,
    var city: String,
    var contact: String,
    var coupon: String,
    var desc: String,
    var id: String,
    var images: List<String>,
    var landmark: String,
    var link: String,
    var name: String,
    var ownerID: String,
    var state: String,
    var type: String
):Serializable