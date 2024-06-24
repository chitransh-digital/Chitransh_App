package com.example.communityapp.data.newModels

import java.io.Serializable

data class KaryaMember(
    val _id: String,
    val designations: List<String>,
    val familyID: String,
    val karyakarni: String,
    val name: String,
    val profilePic: String,
    val contact : String
) : Serializable