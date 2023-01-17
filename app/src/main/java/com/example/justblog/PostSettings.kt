package com.example.justblog


import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amrdeveloper.lottiedialog.LottieDialog
import com.example.justblog.databinding.ActivityPostSettingsBinding
import com.example.justblog.main.ui.MainActivity
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
    private lateinit var view:View
    private lateinit var image:String
    private lateinit var storageReference: StorageReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId:String
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostSettingsBinding.inflate(layoutInflater)
        view=binding.root
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

            val bitmap = (binding.postSettingsImageview.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val originalImage = baos.toByteArray()

            val baosCompress = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baosCompress)
            val compressed = baosCompress.toByteArray()

            val uploadOriginalImgPath=storageReference.child("post_images/${pathHash}.jpg")
            val uploadTask=uploadOriginalImgPath.putBytes(originalImage)
            val uploadCompressedImg=storageReference.child("comp_post_images/${pathHash}.jpg")
            val uploadCompressedImage=uploadCompressedImg.putBytes(compressed)

            binding.postSettingsProgress.visibility=View.VISIBLE
            binding.postSettingsAddDesc.isEnabled=false
            binding.postSettingsImageview.isEnabled=false
            binding.postSettingsCounter.isEnabled=false



            uploadTask.addOnCompleteListener { it1 ->
                if(it1.isSuccessful){
                    uploadOriginalImgPath.downloadUrl.addOnSuccessListener {
                    val originalImgString=it.toString()
                         uploadCompressedImage.addOnCompleteListener { task->
                        if (task.isSuccessful){
                            uploadCompressedImg.downloadUrl.addOnSuccessListener {
                                val postMap: MutableMap<String, Any> = HashMap()
                                postMap["comp_url"]=it.toString()
                                postMap["description"] = binding.postSettingsAddDesc.text.toString()
                                postMap["user_id"] = userId
                                postMap["date"] = FieldValue.serverTimestamp()
                                postMap["type"] = "post"
                                postMap["image_url"] = originalImgString

                                firebaseFirestore.collection("users").document(userId).
                                collection("posts").add(postMap).addOnCompleteListener {
                                    if(it.isSuccessful){
                                     /*   val dialog: LottieDialog = LottieDialog(this)
                                            .setAnimation(R.raw.done)
                                            .setAnimationRepeatCount(LottieDialog.INFINITE)
                                            .setAutoPlayAnimation(true)
                                             .setMessage("Task is Done :D")
                                            .setMessageColor(R.color.light_white)


                                        dialog.show()*/
                                       val intent=Intent(this,MainActivity::class.java)
                                       deleteImage(image)
                                       startActivity(intent)
                                    }
                                    binding.postSettingsProgress.visibility=View.GONE
                                    binding.postSettingsAddDesc.isEnabled=true
                                    binding.postSettingsImageview.isEnabled=true
                                    binding.postSettingsCounter.isEnabled=true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        binding.backButton.setOnClickListener {
            deleteImage(image)
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun deleteImage(image: String) {
        val file = File(getExternalFilesDir("/temp/"), "$image.jpg")
        if(file.exists()){
            file.delete()
        }
    }

    private fun initThis() {
        sharedPreferences=getSharedPreferences("UserInfo",
            Context.MODE_PRIVATE)
        userId=sharedPreferences.getString("userId","").toString()
        storageReference= FirebaseStorage.getInstance().reference;
        image = intent.getStringExtra("image")!!
        binding.postSettingsImageview.setImageBitmap(fileToBitmap(image))

        binding.postSettingsAddDesc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.postSettingsCounter.text=(280- s!!.length).toString()
            }
        })
    }

    private fun fileToBitmap(path: String): Bitmap? {
        val file = File(getExternalFilesDir("/temp/"), "$path.jpg")
        return if(file.exists()){
            BitmapFactory.decodeFile(file.path)
        } else{
            null
        }

    }
}