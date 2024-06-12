package com.example.justblog.data.repository.send_message

import com.example.justblog.data.service.firebase.send_message.SendMessageService
import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class SendMessageRepositoryImpl(
    private val sendMessageService: SendMessageService
) : SendMessageRepository {
    override suspend fun getMessages(usersIds: List<String>): Query {
        return sendMessageService.getMessageList(usersIds)
    }

    override suspend fun sendMessage(hashMap: HashMap<Any, Any>): Task<DocumentReference> {
        return sendMessageService.sendMessage(hashMap)
    }


}