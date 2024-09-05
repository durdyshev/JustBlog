package com.example.justblog.data.service.firebase.chat

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListService(val firebaseFirestore: FirebaseFirestore) {
    private fun getAllChatRoom(): CollectionReference {
        return firebaseFirestore.collection("chatRooms")
    }

    fun getAllChatRoomWhereUserIdExist(userId: String): Query {
        return getAllChatRoom().whereArrayContains("users", userId)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
    }
}