package com.example.justblog.data.model

import java.util.Date

data class MessageData(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val date: Date,
    val users: ArrayList<String>,
    val isRead: Boolean,
    val type: String
)