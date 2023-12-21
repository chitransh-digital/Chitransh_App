package com.example.communityapp.data.models

import java.time.LocalDateTime

data class Comment(
    val name: String,
    val time: LocalDateTime,
    val value: String
)
