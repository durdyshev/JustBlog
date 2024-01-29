package com.example.justblog.main.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.main.model.CommentData
import com.example.justblog.utils.UserCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentsViewModel(application: Application) : BaseViewModel(application) {
    private var commentArrayList = MutableLiveData<ArrayList<CommentData>>()
    val commentDataList: LiveData<ArrayList<CommentData>>
        get() = commentArrayList
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()


    suspend fun getPostCommentsByPostId(postId: String) {
        val commentList = ArrayList<CommentData>()
        CoroutineScope(Dispatchers.IO).launch {
            if (mAuth.currentUser != null) {
                firebaseFirestore.collection("posts").document(postId).collection("comments")
                    .addSnapshotListener { value, error ->
                        if (error == null) {
                            for (doc in value!!) {
                                val comment = CommentData(
                                    commentId = doc.id,
                                    comment = doc.getString("comment"),
                                    date = doc.getTimestamp("date")?.toDate(),
                                    userId = doc.getString("userId")!!
                                )
                                if (!commentList.contains(comment)){
                                    commentList.add(comment)
                                }
                            }
                            val newList = commentList.sortedWith(compareBy { it.date })
                                .reversed()
                            val newArrayList = java.util.ArrayList<CommentData>()
                            newArrayList.addAll(newList)
                            commentArrayList.value = newArrayList
                        }
                    }
            }
        }
    }

    fun addComment(postId: String,comment:String,userCheck: UserCheck) {
        val commentMap: MutableMap<String, Any> = HashMap()
        commentMap["comment"] = comment
        commentMap["userId"] = userCheck.userId()!!
        commentMap["date"] = FieldValue.serverTimestamp()
        firebaseFirestore.collection("posts").document(postId).collection("comments").add(commentMap)
            .addOnCompleteListener {

            }
    }
}