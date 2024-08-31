package com.example.justblog.data.service.firebase.send_message

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SendMessageService(val firebaseFirestore: FirebaseFirestore) {
    fun getMessageList(chatRoomId: String,usersIds: List<String>): Query {
        return firebaseFirestore.collection("chatRooms").document(chatRoomId).collection("messages")
    }

    fun sendMessage(chatRoomId: String,hashMap: HashMap<Any, Any>): Task<DocumentReference> {
        return firebaseFirestore.collection("chatRooms").document(chatRoomId).collection("messages").add(hashMap)
    }

    fun checkChatRoom(chatRoomId: String): Query {
        return firebaseFirestore.collection("chatRooms").whereEqualTo("chatRoomId", chatRoomId)
    }

    fun createChatRoom(chatRoomId: String, hashMap: HashMap<Any, Any>): Task<Void> {
        return firebaseFirestore.collection("chatRooms").document(chatRoomId).set(hashMap)

    }
}