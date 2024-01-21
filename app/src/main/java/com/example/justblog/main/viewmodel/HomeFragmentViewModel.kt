package com.example.justblog.main.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.main.model.PostData
import com.example.justblog.utils.UserCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeFragmentViewModel(application: Application) : BaseViewModel(application), CoroutineScope {
    val app = application
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val userCheck = UserCheck(application.applicationContext)

    private var postArrayList = MutableLiveData<ArrayList<PostData>>()
    val postDataArrayList: LiveData<ArrayList<PostData>>
        get() = postArrayList
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    init {
        CoroutineScope(Dispatchers.IO).launch { getAllPosts() }
    }

    private suspend fun getAllPosts(){
        val postDataArray = ArrayList<PostData>()
        val friendsList = ArrayList<String>()
        friendsList.add(userCheck.userId()!!)
        CoroutineScope(Dispatchers.IO).launch {
            if (mAuth.currentUser != null) {
                firebaseFirestore.collection("users").document(userCheck.userId()!!)
                    .collection("friends").whereEqualTo("status", "2")
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (documentSnapshot in it.result) {
                                friendsList.add(documentSnapshot.id)
                            }
                        }
                        friendsList.forEach {
                            firebaseFirestore.collection("posts").whereEqualTo("user_id", it).get()
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        for (documentSnapshot in it.result) {
                                            val postId = documentSnapshot.id
                                            val compUrl = documentSnapshot.getString("comp_url")
                                            val description =
                                                documentSnapshot.getString("description")
                                            val imageUrl = documentSnapshot.getString("image_url")
                                            val type = documentSnapshot.getString("type")
                                            val userId = documentSnapshot.getString("user_id")
                                            val date = documentSnapshot.getTimestamp("date")
                                            val postData =
                                                PostData(
                                                    postId,
                                                    compUrl,
                                                    description,
                                                    imageUrl,
                                                    type,
                                                    userId,
                                                    date!!.toDate()
                                                )
                                            postDataArray.add(postData)
                                        }

                                        val newList =
                                            postDataArray.sortedWith(compareBy { it.date })
                                                .reversed()
                                        val newArrayList = java.util.ArrayList<PostData>()
                                        newArrayList.addAll(newList)
                                        postArrayList.value = newArrayList
                                    }
                                }
                        }
                    }
            }
        }
    }
}