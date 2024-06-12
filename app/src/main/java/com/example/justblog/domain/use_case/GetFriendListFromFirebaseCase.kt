package com.example.justblog.domain.use_case

import com.example.justblog.data.model.FriendData
import com.example.justblog.domain.repository.new_message_friend_list_repository.NewMessageFriendListRepository
import com.example.justblog.utils.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class GetFriendListFromFirebaseCase(
    private val repository: NewMessageFriendListRepository
) {
    operator fun invoke(userId: String) = callbackFlow<Resource<ArrayList<FriendData>>> {
        repository.getFriendList(userId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(Resource.Success(taskToList(it.result)))
            } else {
                trySend(Resource.Error(it.exception?.message))
            }
        }.await()
        awaitClose { channel.close() }
    }

    private fun taskToList(friendList: QuerySnapshot): ArrayList<FriendData> {
        val friendTransformedList = ArrayList<FriendData>()
        friendList.map {documentSnapshot->
            friendTransformedList.add(
                FriendData(
                    friendId = documentSnapshot.id,
                    status = documentSnapshot.getString("status") ?: "",
                    friendTimestamp = documentSnapshot.getTimestamp("date")?.toDate() ?: Date(),
                )
            )
        }
        return friendTransformedList
    }
}