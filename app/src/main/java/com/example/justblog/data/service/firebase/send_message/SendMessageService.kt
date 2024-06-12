package com.example.justblog.data.service.firebase.send_message

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SendMessageService(val firebaseFirestore: FirebaseFirestore) {
    fun getMessageList(usersIds: List<String>): Query {
        println("idler "+usersIds)
        return firebaseFirestore.collection("messages").whereArrayContainsAny("users", usersIds)
    }

    fun sendMessage(hashMap: HashMap<Any, Any>): Task<DocumentReference> {
        return firebaseFirestore.collection("messages").add(hashMap)
    }
}