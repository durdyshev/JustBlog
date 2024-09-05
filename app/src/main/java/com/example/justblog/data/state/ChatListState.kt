package com.example.justblog.data.state

import com.example.justblog.data.model.ChatData

data class ChatListState(
    val isLoading: Boolean = false,
    val chatList: ArrayList<ChatData> = arrayListOf(),
    val error: String = ""
)
