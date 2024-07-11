package com.example.communityapp.data.newModels

data class SMSRequest(
    val smsContent: String,
    val groupId: String = "0",
    val routeId: String = "1",
    val mobileNumbers: String,
    val senderId: String,
    val signature: String,
    val smsContentType: String = "ENGLISH",
    val entityid: String = "1701162253330843893",
    val tmid: String ="1002408235216785541",
    val templateid: String = "1707162382236268317",
    val concentFailoverId: String = "30"

)