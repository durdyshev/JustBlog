package com.example.justblog.data.service.firebase.new_message_friend_list

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class NewMessageFriendListService(val firebaseFirestore: FirebaseFirestore) {
    fun getFriendList(userId: String): CollectionReference {
        return firebaseFirestore.collection("users").document(userId).collection("friends")
    }
}