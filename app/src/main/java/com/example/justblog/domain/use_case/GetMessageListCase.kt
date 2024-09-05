package com.example.justblog.domain.use_case

import com.example.justblog.data.model.MessageData
import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.example.justblog.utils.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class GetMessageListCase(
    private val repository: SendMessageRepository
) {
    operator fun invoke(chatRoomId:String,usersId: List<String>) =
        callbackFlow<Resource<ArrayList<MessageData>>> {
            val listenerRegistration = repository.getMessages(chatRoomId,usersId)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        trySend(Resource.Success(taskToList(value))).isSuccess
                    } else {
                        trySend(Resource.Error(error?.message)).isSuccess
                    }
                }

            awaitClose {
                listenerRegistration.remove()
            }
        }


    private fun taskToList(friendList: QuerySnapshot): ArrayList<MessageData> {
        val friendTransformedList = ArrayList<MessageData>()
        friendList.map { documentSnapshot ->
            friendTransformedList.add(
                MessageData(
                    id = documentSnapshot.id,
                    senderId = documentSnapshot.getString("senderId") ?: "",
                    receiverId = documentSnapshot.getString("receiverId") ?: "",
                    content = documentSnapshot.getString("content") ?: "",
                    date = documentSnapshot.getTimestamp("date")?.toDate() ?: Date(),
                    users = documentSnapshot.get("users") as? ArrayList<String> ?: arrayListOf(),
                    isRead = documentSnapshot.getBoolean("isRead") ?: false,
                    type = documentSnapshot.getString("type").toString()
                )
            )
        }
        friendList.distinctBy { it.id }
        friendList
        return friendTransformedList
    }
}