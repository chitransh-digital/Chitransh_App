package com.example.communityapp.data.newModels

data class FamilyResponse(
    var count: Int,
    var families: List<FamilyX>,
    var message: String,
    var status: Boolean
)