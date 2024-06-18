package com.example.communityapp.data.newModels

import java.io.Serializable

data class Job(
    var businessName: String,
    var contact: String,
    var externalLink: String,
    var id: String,
    var jobDescription: String,
    var jobTitle: String,
    var location: String,
    var requirements: List<String>,
    var salary: Int
):Serializable