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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justblog.data.model.MessageData
import com.example.justblog.databinding.FragmentSendMessageBinding
import com.example.justblog.main.adapters.SendMessageRecyclerViewAdapter
import com.example.justblog.main.model.ProfileData
import com.example.justblog.main.ui.chat.ChatParent
import com.example.justblog.presentation.viewmodel.send_message.SendMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class SendMessage : Fragment() {
    private lateinit var binding: FragmentSendMessageBinding
    private lateinit var friendId: String
    private lateinit var sendMessageViewModel: SendMessageViewModel
    private lateinit var sendMessageRecyclerViewAdapter: SendMessageRecyclerViewAdapter
    private lateinit var userProfile: ProfileData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendId = requireArguments().get("friendId") as String
        sendMessageViewModel =
            SendMessageViewModel(listOf(FirebaseAuth.getInstance().uid ?: "", friendId))
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
            val content = binding.sendMessageEditText.text.toString()
            if (!TextUtils.isEmpty(content)) {
                sendMessageViewModel.sendMessage(
                    sendMessageViewModel.createHashMap(
                        FirebaseAuth.getInstance().uid,
                        friendId,
                        content
                    )
                )
                binding.sendMessageEditText.setText("")
            }

        }
    }

    private fun initThis() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sendMessageViewModel.messageState.collect { uiState ->
                        println("boy " + uiState.messageList.size)
                        if (uiState.messageList.size != 0) {
                            initRecyclerView(uiState.messageList)
                        }
                    }
                }
                launch {
                    sendMessageViewModel._friendProfile.collect {
                        userProfile = it.profileData ?: ProfileData()
                        Glide.with(binding.newMessageFriendListProfileImg)
                            .load(it.profileData?.profileImg)
                            .into(binding.newMessageFriendListProfileImg)
                        binding.newMessageFriendListUserName.text = it.profileData?.name
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