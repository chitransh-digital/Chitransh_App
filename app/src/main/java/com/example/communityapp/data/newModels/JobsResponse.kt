package com.example.communityapp.data.newModels

data class JobsResponse(
    var count: Int,
    var jobs: List<Job>,
    var status: Boolean
)