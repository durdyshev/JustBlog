package com.example.justblog.domain.repository.chat

import com.google.firebase.firestore.Query

interface ChatListRepository {
    suspend fun getChatList(userId: String): Query
}