package com.example.justblog.main.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.justblog.R
import com.example.justblog.databinding.FragmentChatBinding


class Chat : Fragment() {
    private lateinit var binding: FragmentChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)
        binding.chatPencilSquare.setOnClickListener {
            ChatParent.navController.navigate(R.id.newMessageFriendList2)
        }
        return binding.root
    }

}