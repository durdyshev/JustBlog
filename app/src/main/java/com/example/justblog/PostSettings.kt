package com.example.justblog


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.justblog.databinding.ActivityPostSettingsBinding
import com.example.justblog.main.ui.AddPost
import com.example.justblog.main.ui.MainActivity
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PostSettings : AppCompatActivity() {
    private lateinit var binding: ActivityPostSettingsBinding
    private lateinit var view: View
    private lateinit var storageReference: StorageReference
    private lateinit var userCheck: UserCheck
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSettingsBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        initThis()
        initClickListeners()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initClickListeners() {
        binding.saveButton.setOnClickListener {

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

            val uploadOriginalImgPath = storageReference.child("post_images/${pathHash}.jpg")
            val uploadTask = uploadOriginalImgPath.putBytes(originalImage)
            val uploadCompressedImg = storageReference.child("comp_post_images/${pathHash}.jpg")
            val uploadCompressedImage = uploadCompressedImg.putBytes(compressed)

            binding.postSettingsProgress.visibility = View.VISIBLE
            binding.postSettingsAddDesc.isEnabled = false
            binding.postSettingsImageview.isEnabled = false
            binding.postSettingsCounter.isEnabled = false



            uploadTask.addOnCompleteListener { it1 ->
                if (it1.isSuccessful) {
                    uploadOriginalImgPath.downloadUrl.addOnSuccessListener {
                        val originalImgString = it.toString()

                        uploadCompressedImage.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                uploadCompressedImg.downloadUrl.addOnSuccessListener {
                                    val postMap: MutableMap<String, Any> = HashMap()
                                    postMap["comp_url"] = it.toString()
                                    postMap["description"] =
                                        binding.postSettingsAddDesc.text.toString()
                                    postMap["user_id"] = userCheck.userId()!!
                                    postMap["date"] = FieldValue.serverTimestamp()
                                    postMap["type"] = "photo"
                                    postMap["image_url"] = originalImgString

                                    firebaseFirestore.collection("users").document(userCheck.userId()!!)
                                        .collection("posts").add(postMap).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                /*   val dialog: LottieDialog = LottieDialog(this)
                                                       .setAnimation(R.raw.done)
                                                       .setAnimationRepeatCount(LottieDialog.INFINITE)
                                                       .setAutoPlayAnimation(true)
                                                        .setMessage("Task is Done :D")
                                                       .setMessageColor(R.color.light_white)


                                                   dialog.show()*/
                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                            }
                                            binding.postSettingsProgress.visibility = View.GONE
                                            binding.postSettingsAddDesc.isEnabled = true
                                            binding.postSettingsImageview.isEnabled = true
                                            binding.postSettingsCounter.isEnabled = true
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initThis() {
        userCheck= UserCheck(this)
        storageReference = FirebaseStorage.getInstance().reference;
        binding.postSettingsImageview.setImageBitmap(AddPost.image)

        binding.postSettingsAddDesc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.postSettingsCounter.text = (280 - s!!.length).toString()
            }
        })
    }
}