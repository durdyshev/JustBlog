package com.example.justblog.register.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.justblog.SetupInfo
import com.example.justblog.databinding.ActivityRegisterBinding
import com.example.justblog.register.viewmodel.RegisterViewModel


class Register : AppCompatActivity() {
    private lateinit var registerViewModel:RegisterViewModel
    private lateinit var view:View
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        registerViewModel= ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.regCreateBtn.setOnClickListener {
            val emailText=binding.regEmail.text.toString().trim()
            val passText=binding.regPass.text.toString().trim()
            val passAgainText=binding.regPassAgain.text.toString().trim()
            registerViewModel.registerUser(emailText,passText,passAgainText)

            registerViewModel.result.observe(this) {result->
                if (result) {
                    registerViewModel.registerText.observe(this){
                        Toast.makeText(this,it,Toast.LENGTH_LONG).show()
                        val intent = Intent(this, SetupInfo::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    registerViewModel.registerText.observe(this) {text->
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                    }

                    }
            }

        }

    }
}