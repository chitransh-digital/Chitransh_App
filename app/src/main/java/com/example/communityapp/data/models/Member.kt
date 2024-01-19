package com.example.communityapp.data.models

data class Member(
    val name : String,
    val age : Int,
    val address : String,
    val gender : String,
    val karyakarni : String,
    val familyID : String,
    val contact : String,
    val DOB : String,
    var profilePic : String = "",
    val relation : String
)
