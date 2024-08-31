package com.example.justblog.domain.use_case

import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.example.justblog.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CheckChatRoomCase(
    private val sendMessageRepository: SendMessageRepository
) {
    operator fun invoke(userId: String) = callbackFlow<Resource<Boolean>> {
        sendMessageRepository.checkChatRoom(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(Resource.Success(true))
            } else {
                trySend(Resource.Error(it.exception?.message))
            }
        }.await()
        awaitClose { channel.close() }
    }
}