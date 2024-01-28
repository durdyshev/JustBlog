package com.example.justblog.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import com.example.justblog.MainFragmentPager
import com.example.justblog.databinding.ActivityMainBinding
import com.example.justblog.login.ui.Login
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainFragmentPager
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        hideSystemUI()

        adapter = MainFragmentPager(supportFragmentManager, lifecycle)
        binding.mainViewpager.adapter = adapter
        binding.mainViewpager.setCurrentItem(1,false)
        viewPagerEnable.observe(this){
            binding.mainViewpager.isUserInputEnabled=it
        }


    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(android.R.id.content)
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
    override fun onStart() {
        super.onStart()
        val currentUser= FirebaseAuth.getInstance().currentUser
        if(currentUser==null){
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }
    companion object {
        var viewPagerEnable=MutableLiveData(true)
    }
}
