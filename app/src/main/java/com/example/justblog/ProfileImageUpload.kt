package com.example.justblog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.justblog.databinding.ActivityProfileImageUploadBinding
import com.example.justblog.main.ui.addpost.AddPost
import com.example.justblog.main.ui.main.MainActivity
import com.example.justblog.main.viewmodel.ProfileImageUploadViewModel

class ProfileImageUpload : AppCompatActivity() {
    private lateinit var binding:ActivityProfileImageUploadBinding
    private lateinit var profileImageUploadViewModel: ProfileImageUploadViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileImageUploadBinding.inflate(layoutInflater)
        profileImageUploadViewModel = ViewModelProvider(this)[ProfileImageUploadViewModel::class.java]
        setContentView(binding.root)
        init()
        initClickListener()
    }

    private fun initClickListener() {
        binding.cropButton.setOnClickListener {
            profileImageUploadViewModel.saveProfileImage()
            binding.profileImageUploadProgress.visibility= View.VISIBLE
        }
    }

    private fun init() {
        profileImageUploadViewModel.isLoading.observe(this){
            when(it){
                ImageLoading.IS_FAILED->{
                    Toast.makeText(this,"Image Upload Failed!!!",Toast.LENGTH_LONG).show()
                    binding.profileImageUploadProgress.visibility= View.GONE
                }
                ImageLoading.IS_SUCCESSFUL->{
                    Toast.makeText(this,"Image Upload Successful!!!",Toast.LENGTH_LONG).show()
                    binding.profileImageUploadProgress.visibility= View.GONE
                    val intent= Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                else -> {}
            }
        }
        Glide.with(this).load(AddPost.image).into(binding.profileCircleImageview)
    }
}