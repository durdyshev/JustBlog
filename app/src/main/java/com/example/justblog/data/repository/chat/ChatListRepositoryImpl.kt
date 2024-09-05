package com.example.justblog.data.repository.chat

import com.example.justblog.data.service.firebase.chat.ChatListService
import com.example.justblog.domain.repository.chat.ChatListRepository
import com.google.firebase.firestore.Query

class ChatListRepositoryImpl(
    private val chatListService: ChatListService
) : ChatListRepository {
    override suspend fun getChatList(userId: String): Query {
        return chatListService.getAllChatRoomWhereUserIdExist(userId)
    }
}