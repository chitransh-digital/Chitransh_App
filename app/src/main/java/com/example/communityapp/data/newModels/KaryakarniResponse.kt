package com.example.communityapp.data.newModels

data class KaryakarniResponse(
    val count: Int,
    val karyakarni: List<Karyakarni>,
    val message: String,
    val status: Boolean
)