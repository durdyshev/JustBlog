package com.example.justblog.presentation.views.send_message

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.data.model.MessageData
import com.example.justblog.databinding.FragmentSendMessageBinding
import com.example.justblog.main.adapters.SendMessageRecyclerViewAdapter
import com.example.justblog.main.ui.chat.ChatParent
import com.example.justblog.presentation.viewmodel.send_message.SendMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class SendMessage : Fragment() {
    private lateinit var binding: FragmentSendMessageBinding
    private lateinit var friendId: String
    private lateinit var sendMessageViewModel: SendMessageViewModel
    private lateinit var sendMessageRecyclerViewAdapter: SendMessageRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendId = requireArguments().get("friendId") as String
        sendMessageViewModel =
            SendMessageViewModel(listOf(FirebaseAuth.getInstance().uid?:"", friendId))
        binding = FragmentSendMessageBinding.inflate(inflater, container, false)
        initClickListener()
        initThis()
        return binding.root
    }

    private fun initClickListener() {
        binding.newMessageFriendListBackArrow.setOnClickListener {
            ChatParent.navController.popBackStack()
        }
        binding.sendMessageSendButton.setOnClickListener {
            if (!TextUtils.isEmpty(binding.sendMessageEditText.text)) {
                sendMessageViewModel.sendMessage(
                    sendMessageViewModel.createHashMap(
                        FirebaseAuth.getInstance().uid,
                        friendId,
                        binding.sendMessageEditText.text.toString()
                    )
                )
            }

        }
    }

    private fun initThis() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                sendMessageViewModel.messageState.collect { uiState ->
                    println("boy "+uiState.messageList.size)
                    if (uiState.messageList.size != 0) {
                        initRecyclerView(uiState.messageList)
                    }
                }
            }
        }
    }

    private fun initRecyclerView(messageList: ArrayList<MessageData>) {
        sendMessageRecyclerViewAdapter =
            SendMessageRecyclerViewAdapter(requireContext(), messageList)
        binding.sendMessageRecyclerview.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.sendMessageRecyclerview.adapter = sendMessageRecyclerViewAdapter
    }
}