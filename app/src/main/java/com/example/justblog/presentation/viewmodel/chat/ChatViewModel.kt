package com.example.justblog.presentation.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justblog.data.state.ChatListState
import com.example.justblog.domain.use_case.GetChatListCase
import com.example.justblog.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatViewModel : ViewModel(), KoinComponent {
    private val getChatListCase: GetChatListCase by inject()
    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state

    init {
        getChatList()
    }

    private fun getChatList() {
        viewModelScope.launch {
            getChatListCase(userId = FirebaseAuth.getInstance().uid!!).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value =
                            ChatListState(
                                chatList = result.data ?: arrayListOf(),
                                isLoading = false
                            )

                    }

                    is Resource.Error -> {
                        _state.value = ChatListState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {
                        _state.value = ChatListState(isLoading = true)
                    }
                }
            }
        }
    }
}