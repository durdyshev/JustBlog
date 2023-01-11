package com.example.justblog.register.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RegisterViewModel(application: Application): BaseViewModel(application) {

      val registerText=MutableLiveData<String>()
      val result=MutableLiveData<Boolean>()
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    private val firebaseFirestore:FirebaseFirestore=FirebaseFirestore.getInstance()
    private val sharedPreferences:SharedPreferences=application.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
    private val sharedEditor:Editor=sharedPreferences.edit()

    fun registerUser(email: String, password: String, password2: String) {

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password2)) {
            if (password == password2) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val userId: String = mAuth.currentUser!!.uid
                        val deviceToken: String = FirebaseMessaging.getInstance().token.toString()
                        sharedEditor.putString("userId",deviceToken)
                        sharedEditor.apply()

                        val userMap: MutableMap<String, Any> = HashMap()
                        userMap["token"] = deviceToken
                        userMap["typing"] = ""
                        userMap["contacts"] = ""
                        userMap["background"] = ""
                        userMap["motto"] = "Hey there I am using BlogApp"
                        userMap["phone"] = ""
                        firebaseFirestore.collection("users").document(userId).set(
                            userMap
                        ).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                result.value=true
                                registerText.value="Account has been created!"
                            }
                            else{
                                result.value=false
                                registerText.value="Problem has occurred!"
                            }
                        }
                    }
                    else {
                        result.value=false
                        registerText.value="Problem has occurred!"
                            }
                        }
            } else {
               result.value=false
               registerText.value="Passwords are not equal!"
            }
        } else {
            result.value=false
            registerText.value="Please fill all empty places!"
        }

    }
}
