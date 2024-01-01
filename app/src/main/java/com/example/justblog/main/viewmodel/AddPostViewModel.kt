package com.example.justblog.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.justblog.BaseViewModel
import com.example.justblog.CryptAndHashAlgorithm
import com.example.justblog.main.model.Bucket
import com.example.justblog.main.ui.AddPost
import com.example.justblog.main.ui.MainActivity
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import kotlin.coroutines.CoroutineContext


/**
 * Use Coroutines To Load Images
 */
class AddPostViewModel(application: Application) : BaseViewModel(application), CoroutineScope {

    private val application1 = application
    private var imagesLiveData: MutableLiveData<List<String>> = MutableLiveData()
    private var directoriesLiveData: MutableLiveData<ArrayList<Bucket>> = MutableLiveData()
    var imageLists: MutableLiveData<ArrayList<String>> = MutableLiveData()
    private var storageReference = FirebaseStorage.getInstance().reference
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCheck = UserCheck(application.applicationContext)

    fun getImageList(): MutableLiveData<List<String>> {
        return imagesLiveData
    }

    fun getDirectoriesList(): MutableLiveData<ArrayList<Bucket>> {
        return directoriesLiveData
    }

    /**
     * Getting All Images Path.
     *
     * Required Storage Permission
     *
     * @return ArrayList with images Path
     */
    @SuppressLint("Recycle")
    private fun loadImagesFromSDCard(): ArrayList<String> {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val columnIndexData: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = application1.contentResolver!!.query(uri, projection, null, null, null)

        columnIndexData = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(columnIndexData)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }

    fun getAllImages() {
        launch(Dispatchers.Main) {
            imagesLiveData.value = withContext(Dispatchers.IO) {
                loadImagesFromSDCard()
            }!!
        }
    }

    fun getAllDirectories() {
        launch(Dispatchers.Main) {
            directoriesLiveData.value = withContext(Dispatchers.IO) {
                getImageDirectories()
            }!!
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    private fun getImageDirectories(): ArrayList<Bucket> {
        val directories: ArrayList<String> = ArrayList()
        val directories1: ArrayList<Bucket> = ArrayList()
        val imagesList: ArrayList<String> = ArrayList()
        val contentResolver: ContentResolver = application1.contentResolver
        val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val includeImages = MediaStore.Images.Media.MIME_TYPE + " LIKE 'image/%' "
        val excludeGif =
            " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/gif' " + " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/giff' "
        val selection = includeImages + excludeGif
        val orderBy = MediaStore.Images.Media.DATE_TAKEN

        val cursor = contentResolver.query(queryUri, projection, selection, null, "$orderBy DESC")
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val photoUri = cursor.getString(0)
                val photoFolderPath = File(photoUri).parent!!
                imagesList.add(File(photoUri).absolutePath)
                if (!directories.contains(photoFolderPath)) {
                    val folderName: MutableList<String> =
                        File(photoUri).parent!!.split("/").toMutableList()

                    val bucket = Bucket(folderName.last(), photoFolderPath)
                    directories.add(File(photoUri).parent!!)
                    directories1.add(bucket)
                }
            } while (cursor.moveToNext())
            imageLists.postValue(imagesList)
        }
        return directories1
    }

    fun saveImg() {
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
                                profile["profile_img"] = originalImgString

                                firebaseFirestore.collection("users").document(userCheck.userId()!!)
                                    .update(profile).addOnCompleteListener {
                                        if (it.isSuccessful) {

                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

}