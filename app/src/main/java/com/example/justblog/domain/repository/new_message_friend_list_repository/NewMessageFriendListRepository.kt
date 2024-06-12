package com.example.justblog.domain.repository.new_message_friend_list_repository

import com.google.firebase.firestore.CollectionReference

interface NewMessageFriendListRepository {
    suspend fun getFriendList(userId: String): CollectionReference
}