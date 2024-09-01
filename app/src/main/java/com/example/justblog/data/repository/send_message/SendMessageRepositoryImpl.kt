package com.example.justblog.data.repository.send_message

import com.example.justblog.data.service.firebase.send_message.SendMessageService
import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class SendMessageRepositoryImpl(
    private val sendMessageService: SendMessageService
) : SendMessageRepository {
    override suspend fun getMessages(chatRoomId: String, usersIds: List<String>): Query {
        return sendMessageService.getMessageList(chatRoomId, usersIds)
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        hashMap: HashMap<Any, Any>
    ): Task<DocumentReference> {
        return sendMessageService.sendMessage(chatRoomId, hashMap)
    }

    override suspend fun getProfileData(userId: String): Query {
        return sendMessageService.getProfileData(userId)
    }
}