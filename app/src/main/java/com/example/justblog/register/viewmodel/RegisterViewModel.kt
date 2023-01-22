package com.example.justblog.register.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.FirebaseDb
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RegisterViewModel(application: Application): BaseViewModel(application) {

    val registerText=MutableLiveData<String>()
    val registerResult=MutableLiveData<Boolean>()
    val checkEmailResult=MutableLiveData<Boolean>()
    val checkEmailString=MutableLiveData<String>()
    val checkUserResult=MutableLiveData<Int>()
    val checkUserString=MutableLiveData<String>()
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    private val firebaseFirestore:FirebaseFirestore=FirebaseFirestore.getInstance()
    private val sharedPreferences:SharedPreferences=application.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
    private val sharedEditor:Editor=sharedPreferences.edit()
    private val firebaseDb=FirebaseDb(application)

    fun registerUser(email: String, password: String,name:String,username: String) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                val userId: String = mAuth.currentUser!!.uid
                val deviceToken: String = FirebaseMessaging.getInstance().token.toString()
                sharedEditor.putString("userId",userId)
                sharedEditor.apply()

                val userMap: MutableMap<String, Any> = HashMap()
                userMap["token"] = deviceToken
                userMap["userId"] = userId
                userMap["name"] = name
                userMap["username"] = username
                userMap["typing"] = ""
                userMap["contacts"] = ""
                userMap["background"] = ""
                userMap["motto"] = "Hey there I am using BlogApp"
                userMap["phone"] = ""
                firebaseFirestore.collection("users").document(userId).set(
                    userMap
                ).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        registerResult.value=true
                        registerText.value="Account has been created!"
                    }
                    else{
                        registerResult.value=false
                        registerText.value="Problem has occurred!"
                    }
                }
            }
            else {
                registerResult.value=false
                registerText.value="Problem has occurred!"
            }
        }

    }

    fun checkEmailExistsOrNot(emailText: String) {

        mAuth.fetchSignInMethodsForEmail(emailText.trim()).addOnCompleteListener {
            if(it.result.signInMethods!!.size==0){
                //registerUser(emailText)
                checkEmailResult.value=true

            }
            else{
                checkEmailResult.value=false
                checkEmailString.value="Email is already in use!"
            }
        }
    }
    fun checkUserNameExistsOrNot(username: String) {
        firebaseFirestore.collection("users").whereEqualTo("username",username).get().addOnCompleteListener {
            if(!it.result.isEmpty){
                checkUserResult.value=it.result.size()
                checkUserString.value="Username is already in use!"
            }
            else{
                checkUserResult.value=0

            }
        }
    }
}
