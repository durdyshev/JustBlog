package com.example.justblog

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.justblog.databinding.ActivityPostSettingsBinding
import com.example.justblog.main.ui.MainActivity
import java.io.File


class PostSettings : AppCompatActivity() {
    private lateinit var binding: ActivityPostSettingsBinding
    private lateinit var view:View
    private lateinit var image:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPostSettingsBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        initThis()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.saveButton.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            deleteImage(image)
            startActivity(intent)
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
        image = intent.getStringExtra("image")!!
        binding.postSettingsImageview.setImageBitmap(fileToBitmap(image))
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