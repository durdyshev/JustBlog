package com.example.justblog.presentation.views.new_message_friend_list

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
import com.example.justblog.data.model.FriendData
import com.example.justblog.databinding.FragmentNewMessageFriendListBinding
import com.example.justblog.main.adapters.ProfileListRecyclerViewAdapter
import com.example.justblog.main.ui.chat.ChatParent
import com.example.justblog.presentation.viewmodel.new_message_friend_list.NewMessageFriendListViewModel
import kotlinx.coroutines.launch

class NewMessageFriendList : Fragment() {
    private lateinit var binding: FragmentNewMessageFriendListBinding
    private lateinit var newMessageFriendListViewModel: NewMessageFriendListViewModel
    private lateinit var profileListRecyclerViewAdapter: ProfileListRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewMessageFriendListBinding.inflate(layoutInflater, container, false)
        initThis()
        return binding.root
    }

    private fun initThis() {
        newMessageFriendListViewModel = NewMessageFriendListViewModel()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newMessageFriendListViewModel.state.collect { uiState ->
                    if (uiState.friendList.size != 0) {
                        initRecyclerView(uiState.friendList)
                    }
                }
            }
        }
        binding.newMessageFriendListBackArrow.setOnClickListener {
            ChatParent.navController.popBackStack()
        }
    }

    private fun initRecyclerView(friendList: ArrayList<FriendData>) {
        friendList.filter { it.status == "2" }
        profileListRecyclerViewAdapter =
            ProfileListRecyclerViewAdapter(requireContext(), friendList)
        binding.newMessageFriendListRecyclerview.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.newMessageFriendListRecyclerview.adapter = profileListRecyclerViewAdapter
        profileListRecyclerViewAdapter.setOnClickItem {
            val bundle = Bundle()
            bundle.putString("friendId", it.friendId)
            ChatParent.navController.navigate(R.id.sendMessage, bundle)
        }
    }
}