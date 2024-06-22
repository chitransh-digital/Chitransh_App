package com.example.communityapp.data.newModels

data class Karyakarni(
    val address: String,
    val city: String,
    val designations: List<String>,
    val id: String,
    val landmark: String,
    val level: String,
    val logo: String,
    val members: List<KaryaMember>,
    val name: String,
    val state: String
)