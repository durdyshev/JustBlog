package com.example.justblog.main.viewmodel

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.BaseViewModel
import com.example.justblog.R
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.model.PostData
import com.example.justblog.main.model.ProfileData
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    private var status = MutableLiveData<String>()
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    val statusString: LiveData<String>
        get() = status
    private var image = MutableLiveData<Int>()
    val imageValue: LiveData<Int>
        get() = image
    var buttonEnable = MutableLiveData<Boolean>()

    private var postArrayList = MutableLiveData<ArrayList<PostData>>()
    val postDataArrayList: LiveData<ArrayList<PostData>>
        get() = postArrayList

    fun friendExistenceCheck(userCheck: UserCheck, profileData: ProfileData) {
        val userExistenceInFriends =
            firebaseFirestore.collection("/users/${userCheck.userId()}/friends")
                .document(profileData.userId!!)
        userExistenceInFriends.get().addOnCompleteListener {
            if (it.result.exists()) {
                when (it.result.getString("status")) {
                    "0" -> {
                        status.value = "Sent Request"
                        image.value = R.drawable.ic_main_button_background

                    }

                    "1" -> {
                        status.value = "Request Came"
                        image.value = R.drawable.ic_main_button_background

                    }

                    "2" -> {
                        status.value = "Your Friend"
                        image.value =
                            R.drawable.ic_arena_recyclerview_linear_layout_textview_background

                    }
                }
            } else {
                status.value = "Add Friend"
                image.value = R.drawable.ic_arena_recyclerview_linear_layout_textview_background

            }
        }
    }

    fun userProfileClick(userCheck: UserCheck, profileData: ProfileData, context: Context) {
        buttonEnable.value = false
        val userExistenceInFriends =
            firebaseFirestore.collection("/users/${userCheck.userId()}/friends")
                .document(profileData.userId!!)
        userExistenceInFriends.get().addOnCompleteListener {
            if (!it.result.exists()) {
                //If user is not your friend
                val firstFriendMap: MutableMap<String, Any> = HashMap()
                firstFriendMap["date"] = FieldValue.serverTimestamp()
                firstFriendMap["status"] = "0"
                val secondFriendMap: MutableMap<String, Any> = HashMap()
                secondFriendMap["date"] = FieldValue.serverTimestamp()
                secondFriendMap["status"] = "1"
                userExistenceInFriends.set(firstFriendMap)
                firebaseFirestore.collection("/users/${profileData.userId}/friends")
                    .document(userCheck.userId()!!).set(secondFriendMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                            status.value = "Friend Request"
                            image.value = R.drawable.ic_main_button_background
                            buttonEnable.value = true
                        } else {
                            buttonEnable.value = true
                        }
                    }
            } else {
                //If user is your friend
                when (it.result.get("status")) {
                    "0" -> {
                        showFriendDeclineOrDelete(
                            context,
                            R.layout.bottom_sheet_sign_out,
                            userExistenceInFriends, "Decline",
                            userCheck, profileData
                        )
                    }

                    "1" -> {
                        showFriendAcceptance(
                            context,
                            R.layout.bottom_sheet_friend_acceptance,
                            userExistenceInFriends,
                            userCheck,
                            profileData
                        )
                    }

                    "2" -> {
                        showFriendDeclineOrDelete(
                            context,
                            R.layout.bottom_sheet_sign_out,
                            userExistenceInFriends, "Delete",
                            userCheck,
                            profileData
                        )
                    }
                }

            }
        }
    }

    private fun showFriendDeclineOrDelete(
        context: Context,
        layoutId: Int,
        userExistenceInFriends: DocumentReference,
        s: String,
        userCheck: UserCheck,
        profileData: ProfileData
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutId)
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        val decline = dialog.findViewById<Button>(R.id.bottom_sheet_sign_out)
        decline.text = s
        decline.setOnClickListener {
            userExistenceInFriends.delete()
            firebaseFirestore.collection("/users/${profileData.userId}/friends")
                .document(userCheck.userId()!!).delete().addOnCompleteListener {
                    status.value = "Add Friend"
                    image.value = R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                    buttonEnable.value = true
                    dialog.dismiss()
                }
        }
    }

    private fun showFriendAcceptance(
        context: Context,
        layoutId: Int,
        userExistenceInFriends: DocumentReference,
        userCheck: UserCheck,
        profileData: ProfileData
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutId)
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        val accept = dialog.findViewById<Button>(R.id.bottom_sheet_accept_friend)
        val decline = dialog.findViewById<Button>(R.id.bottom_sheet_decline_friend)
        accept.setOnClickListener {
            val firstFriendMap: MutableMap<String, Any> = java.util.HashMap()
            firstFriendMap["date"] = FieldValue.serverTimestamp()
            firstFriendMap["status"] = "2"
            val secondFriendMap: MutableMap<String, Any> = java.util.HashMap()
            secondFriendMap["date"] = FieldValue.serverTimestamp()
            secondFriendMap["status"] = "2"
            userExistenceInFriends.update(firstFriendMap)
            firebaseFirestore.collection("/users/${profileData.userId}/friends")
                .document(userCheck.userId()!!).update(secondFriendMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        status.value = "Friend"
                        image.value = R.drawable.ic_main_button_background
                        buttonEnable.value = true
                        dialog.dismiss()
                    } else {
                        buttonEnable.value = true
                        dialog.dismiss()
                    }
                }
        }
        decline.setOnClickListener {
            userExistenceInFriends.delete()
            firebaseFirestore.collection("/users/${profileData.userId}/friends")
                .document(userCheck.userId()!!).delete().addOnCompleteListener {
                    status.value = "Add Friend"
                    image.value = R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                    buttonEnable.value = true
                    dialog.dismiss()

                }
        }
    }

    fun getUserPosts(profileData: ProfileData) {

        val postDataArrayList = ArrayList<PostData>()
        firebaseFirestore.collection("posts").whereEqualTo("user_id",profileData.userId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (documentSnapshot in it.result) {
                        val postId = documentSnapshot.id
                        val compUrl = documentSnapshot.getString("comp_url")
                        val description = documentSnapshot.getString("description")
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
                        postDataArrayList.add(postData)
                    }
                    val newList = postDataArrayList.sortedWith(compareBy { it.date }).reversed()
                    val newArrayList = java.util.ArrayList<PostData>()
                    newArrayList.addAll(newList)
                    postArrayList.value = newArrayList

                }
            }
    }
}