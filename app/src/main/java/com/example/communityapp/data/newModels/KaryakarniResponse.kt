package com.example.communityapp.data.newModels

import java.io.Serializable

data class KaryakarniResponse(
    val count: Int,
    val karyakarni: List<Karyakarni>,
    val message: String,
    val status: Boolean
) : Serializable