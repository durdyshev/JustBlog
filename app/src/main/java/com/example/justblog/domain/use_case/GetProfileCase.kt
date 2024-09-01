package com.example.justblog.domain.use_case

import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.example.justblog.main.model.ProfileData
import com.example.justblog.utils.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class GetProfileCase(
    private val repository: SendMessageRepository
) {
    operator fun invoke(userId: String) =
        callbackFlow<Resource<ProfileData>> {
            val listenerRegistration = repository.getProfileData(userId)
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


    private fun taskToList(friendList: QuerySnapshot): ProfileData {
        val documentSnapshot = friendList.documents.firstOrNull()
        return ProfileData(
            name = documentSnapshot?.getString("name") ?: "",
            username = documentSnapshot?.getString("username") ?: "",
            motto = documentSnapshot?.getString("motto") ?: "",
            profileImg = documentSnapshot?.getString("profile_img") ?: "",
            userId = documentSnapshot?.getString("user_id") ?: "",
        )
    }
}