package com.example.justblog.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.justblog.databinding.ActivityLoginBinding
import com.example.justblog.login.viewmodel.LoginViewModel
import com.example.justblog.register.ui.Register

class Login : AppCompatActivity() {
    private lateinit var loginActivityViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        loginActivityViewModel= ViewModelProvider(this)[LoginViewModel::class.java]

        binding.regKnopka.setOnClickListener {
            val intent= Intent(this,Register::class.java)
            startActivity(intent)
        }
    }
}