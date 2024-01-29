package com.example.justblog.main.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.CryptAndHashAlgorithm
import com.example.justblog.ImageLoading
import com.example.justblog.main.ui.addpost.AddPost
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext

class ProfileImageUploadViewModel(application: Application) : BaseViewModel(application),
    CoroutineScope {
    private var storageReference = FirebaseStorage.getInstance().reference
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCheck = UserCheck(application.applicationContext)
    private var isLoadingData = MutableLiveData(ImageLoading.NONE)
    val isLoading: LiveData<ImageLoading>
        get() = isLoadingData
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    fun saveProfileImage() {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val currentHourString = sdf.format(Date())
        val pathHash = CryptAndHashAlgorithm.Hash.md5(currentHourString)

        val bitmap = AddPost.image
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val originalImage = baos.toByteArray()

        val baosCompress = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baosCompress)
        val compressed = baosCompress.toByteArray()

        val uploadOriginalImgPath = storageReference.child("profile_images/${pathHash}.jpg")
        val uploadTask = uploadOriginalImgPath.putBytes(originalImage)
        val uploadCompressedImg = storageReference.child("comp_profile_images/${pathHash}.jpg")
        val uploadCompressedImage = uploadCompressedImg.putBytes(compressed)

        uploadTask.addOnCompleteListener { it1 ->
            if (it1.isSuccessful) {
                uploadOriginalImgPath.downloadUrl.addOnSuccessListener {
                    val originalImgString = it.toString()

                    uploadCompressedImage.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            uploadCompressedImg.downloadUrl.addOnSuccessListener {
                                val profile: MutableMap<String, Any> = HashMap()
                                profile["profile_img"] = it.toString()

                                firebaseFirestore.collection("users").document(userCheck.userId()!!)
                                    .update(profile).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            isLoadingData.value = ImageLoading.IS_SUCCESSFUL
                                        } else {
                                            isLoadingData.value = ImageLoading.IS_FAILED
                                        }
                                        return@addOnCompleteListener
                                    }
                            }
                        }
                    }
                }
            }else{
                isLoadingData.value = ImageLoading.IS_FAILED
            }
        }
    }

}