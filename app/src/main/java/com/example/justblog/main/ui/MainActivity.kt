package com.example.justblog.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.justblog.R
import com.example.justblog.databinding.ActivityMainBinding
import com.example.justblog.login.ui.Login
import com.example.justblog.main.viewmodel.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: View
    private lateinit var navController: NavController
    private lateinit var navHostFragment:NavHostFragment
    private lateinit var mainActivityViewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
        hideSystemUI()
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setupWithNavController(navController)

        mainActivityViewModel=ViewModelProvider(this)[MainActivityViewModel::class.java]
        mainActivityViewModel.getFirebaseAuth()
        observeLiveData()
    }
    private fun observeLiveData() {
        mainActivityViewModel.currentUserValue.observe(this, Observer {
            it?.let {
             if(it==null){

             }
            }
        })
        mainActivityViewModel.text.observe(this, Observer {
            it.let {
                Toast.makeText(this,it,Toast.LENGTH_LONG).show()
            }
        })
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
  }
