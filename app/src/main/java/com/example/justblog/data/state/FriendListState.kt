package com.example.justblog.data.state

import com.example.justblog.data.model.FriendData

data class FriendListState(
    val isLoading: Boolean = false,
    val friendList: ArrayList<FriendData> = arrayListOf(),
    val error: String = ""
)