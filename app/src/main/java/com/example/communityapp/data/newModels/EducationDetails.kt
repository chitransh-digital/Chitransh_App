package com.example.communityapp.data.newModels

import java.io.Serializable

data class EducationDetails(
    val additionalDetails: String,
    val course: String,
    val fieldOfStudy: String,
    val institute: String
) : Serializable