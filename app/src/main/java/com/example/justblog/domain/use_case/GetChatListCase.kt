package com.example.justblog.domain.use_case

import com.example.justblog.data.model.ChatData
import com.example.justblog.domain.repository.chat.ChatListRepository
import com.example.justblog.utils.CheckUserId
import com.example.justblog.utils.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class GetChatListCase(
    private val repository: ChatListRepository
) {
    operator fun invoke(userId: String) = callbackFlow<Resource<ArrayList<ChatData>>> {
        repository.getChatList(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(Resource.Success(taskToList(it.result,userId)))
            } else {
                trySend(Resource.Error(it.exception?.message))
            }
        }.await()
        awaitClose { channel.close() }
    }

    private fun taskToList(friendList: QuerySnapshot, userId: String): ArrayList<ChatData> {
        val friendTransformedList = ArrayList<ChatData>()
        friendList.map { documentSnapshot ->
            val chatData = ChatData(
                chatRoomId = documentSnapshot.getString("chatRoomId") ?: "",
                lastMessageSenderId = documentSnapshot.getString("lastMessageSenderId") ?: "",
                lastMessageTimeStamp = documentSnapshot.getTimestamp("lastMessageTimeStamp")?.toDate() ?: Date(),
                usersId = documentSnapshot.get("users") as? ArrayList<String> ?: arrayListOf(),
            )
            chatData.senderId = CheckUserId.checkUserId(chatData.usersId, userId = userId)
            friendTransformedList.add(chatData)
        }
        return friendTransformedList
    }
}