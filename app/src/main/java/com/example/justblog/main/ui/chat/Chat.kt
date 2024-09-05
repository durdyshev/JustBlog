package com.example.justblog.main.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.R
import com.example.justblog.data.model.ChatData
import com.example.justblog.databinding.FragmentChatBinding
import com.example.justblog.main.adapters.ChatRecyclerViewAdapter
import com.example.justblog.main.adapters.ProfileListRecyclerViewAdapter
import com.example.justblog.main.ui.main.MainActivity
import com.example.justblog.presentation.viewmodel.chat.ChatViewModel
import com.example.justblog.presentation.viewmodel.new_message_friend_list.NewMessageFriendListViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList


class Chat : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        binding.chatPencilSquare.setOnClickListener {
            ChatParent.navController.navigate(R.id.newMessageFriendList2)
        }
        binding.backArrow.setOnClickListener {
            MainActivity.mainViewPager.setCurrentItem(1, true)
        }
        initThis()
        return binding.root
    }

    private fun initThis() {
        chatViewModel = ChatViewModel()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.state.collect { uiState ->
                    if (uiState.chatList.size != 0) {
                        initRecyclerView(uiState.chatList)
                    }
                }
            }
        }
    }

    private fun initRecyclerView(chatList: ArrayList<ChatData>) {
        chatRecyclerViewAdapter =
            ChatRecyclerViewAdapter(requireContext(), chatList)
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.chatRecyclerview.adapter = chatRecyclerViewAdapter
        chatRecyclerViewAdapter.setOnClickItem {
            val bundle = Bundle()
            bundle.putString("friendId", it.senderId)
            ChatParent.navController.navigate(R.id.sendMessage, bundle)
        }

    }

}