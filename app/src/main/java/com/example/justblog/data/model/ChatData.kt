package com.example.justblog.data.model

import java.util.Date

data class ChatData(
    val chatRoomId: String,
    val lastMessageSenderId: String,
    val lastMessageTimeStamp: Date,
    val usersId: ArrayList<String>,
    var senderId: String?=""
)