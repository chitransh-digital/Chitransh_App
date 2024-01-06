package com.example.communityapp.data.models

data class NewsFeed (
    val author: String,
    val body: String,
    val images: List<String>,
    val timestamp: String,
    val title: String,
    val visible: Boolean
)