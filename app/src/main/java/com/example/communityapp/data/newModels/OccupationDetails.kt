package com.example.communityapp.data.newModels

import java.io.Serializable

data class OccupationDetails(
    val jobDepartment: String,
    val jobEmployer: String,
    val jobLocation: String,
    val jobPost: String,
    val businessName : String,
    val businessType : String,
    val businessAddress : String
) : Serializable