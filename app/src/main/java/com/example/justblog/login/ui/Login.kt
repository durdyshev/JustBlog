package com.example.justblog.login.ui

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
import com.example.justblog.databinding.ActivityLoginBinding
import com.example.justblog.login.viewmodel.LoginViewModel
import com.example.justblog.main.ui.MainActivity
import com.example.justblog.register.ui.Register

class Login : AppCompatActivity() {
    private lateinit var loginActivityViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        hideSystemUI()
        loginActivityViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.loginBtn.setOnClickListener {

            val email = binding.logEmail.text.toString().trim()
            val pass = binding.logPass.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                binding.logEmail.error = "Please fill in the blanks!!"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(pass)) {
                binding.logEmail.error = "Please fill in the blanks!!"
                return@setOnClickListener
            } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                loginActivityViewModel.loginUser(email, pass)
                loginActivityViewModel.loginBoolean.observe(this) {
                    if (it) {
                        Toast.makeText(
                            this, "Login successfully", Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        loginActivityViewModel.loginString.observe(this) { text ->
                            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        binding.logRegPage.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
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
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            hideSystemUI()
        }
    }
}