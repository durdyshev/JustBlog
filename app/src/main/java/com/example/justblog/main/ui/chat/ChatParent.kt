package com.example.justblog.main.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.justblog.R
import com.example.justblog.databinding.FragmentChatParentBinding

class ChatParent : Fragment() {
    private lateinit var binding: FragmentChatParentBinding
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatParentBinding.inflate(layoutInflater, container, false)
        navHostFragment = childFragmentManager.findFragmentById(R.id.chat_parent_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        return binding.root
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var navController: NavController
    }
}