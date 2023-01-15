package com.example.justblog.register.ui

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.justblog.SetupInfo
import com.example.justblog.databinding.ActivityRegisterBinding
import com.example.justblog.main.ui.MainActivity
import com.example.justblog.register.viewmodel.RegisterViewModel


class Register : AppCompatActivity() {
    private lateinit var registerViewModel:RegisterViewModel
    private lateinit var view:View
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var emailString:String
    private lateinit var passString:String
    private lateinit var userNameString:String
    private lateinit var nameString:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        hideSystemUI()
        registerViewModel= ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.registerLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.regCreateBtn.setOnClickListener {
            if(binding.registerEmailLinear.visibility==View.VISIBLE && !TextUtils.isEmpty(binding.regEmail.text)){

                registerViewModel.checkEmailExistsOrNot(binding.regEmail.text.toString().trim())
                registerViewModel.checkEmailResult.observe(this) {result->
                    if (result){
                        emailString=binding.regEmail.text.toString().trim()
                        binding.registerEmailLinear.visibility=View.GONE
                        binding.registerNameUserNameLinear.visibility=View.GONE
                        binding.registerNameLinear.visibility=View.VISIBLE
                        binding.regCreateBtn.text="Next"

                    }
                    else{
                        registerViewModel.checkEmailString.observe(this) {text->
                            if(text!=null){
                            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                        }
                        }

                    }
                }
            }
            else if(binding.registerNameLinear.visibility==View.VISIBLE
                && !TextUtils.isEmpty(binding.regName.text.toString().trim())
                && !TextUtils.isEmpty(binding.regPass.text.toString().trim())){
                nameString=binding.regName.text.toString().trim()
                passString=binding.regPass.text.toString().trim()
                binding.registerEmailLinear.visibility=View.GONE
                binding.registerNameUserNameLinear.visibility=View.VISIBLE
                binding.registerNameLinear.visibility=View.GONE
                binding.regCreateBtn.text="Register"
            }

            else if(binding.registerNameUserNameLinear.visibility==View.VISIBLE
                && !TextUtils.isEmpty(binding.registerNameUserName.text)){
                userNameString=binding.registerNameUserName.text.toString().trim()

                registerViewModel.checkUserNameExistsOrNot(binding.registerNameUserName.text.toString().trim())
                registerViewModel.checkUserResult.observe(this) {result->
                    if (result!=0){
                        registerViewModel.checkUserString.observe(this) {text->
                            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                        }
                    }
                    else{
                        registerViewModel.registerUser(
                            emailString,passString,nameString,userNameString
                        )
                        registerViewModel.registerResult.observe(this) {result->
                            if (result) {
                                registerViewModel.registerText.observe(this){
                                    Toast.makeText(this,it,Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, MainActivity::class.java)
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

        }
    }
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(R.id.content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        hideSystemKeyboard()
        onWindowFocusChanged(true)
        return super.dispatchTouchEvent(ev)
    }
    private fun hideSystemKeyboard(){
        if(currentFocus!=null){
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

    }

}