package com.example.justblog

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.justblog.databinding.ActivitySetupInfosBinding

class SetupInfo : AppCompatActivity() {
    private lateinit var binding: ActivitySetupInfosBinding
    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySetupInfosBinding.inflate(layoutInflater)
        view=binding.root
        setContentView(view)
    }
}