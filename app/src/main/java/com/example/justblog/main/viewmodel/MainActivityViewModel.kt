package com.example.justblog.main.viewmodel

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.R
import com.example.justblog.login.ui.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.security.AccessController.getContext


class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    val currentUserValue = MutableLiveData<FirebaseUser?>()
    val text = MutableLiveData<String>()
    val application1 = application
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getFirebaseAuth() {
    }

    fun showSelectImageDialog(context: Context, layoutId: Int) {
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
        val signOut = dialog.findViewById<Button>(R.id.bottom_sheet_sign_out)
        signOut.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                signOut(context,dialog)
            }
        }
    }

    private suspend fun signOut(context: Context, dialog: Dialog) {
        CoroutineScope(Dispatchers.Main).async { return@async firebaseAuth.signOut() }.await()
        dialog.dismiss()
        val intent = Intent(context, Login::class.java)
        context.startActivity(intent)
        try {
            val activity = context as Activity
            activity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}