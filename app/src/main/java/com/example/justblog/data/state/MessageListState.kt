package com.example.justblog.data.state

import com.example.justblog.data.model.MessageData

data class MessageListState(
    val isLoading: Boolean = false,
    val messageList: ArrayList<MessageData> = arrayListOf(),
    val error: String = ""
)