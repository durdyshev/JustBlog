package com.example.justblog.domain.repository.send_message

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

interface SendMessageRepository {
    suspend fun getMessages(usersIds: List<String>): Query
    suspend fun sendMessage(hashMap: HashMap<Any, Any>): Task<DocumentReference>

}