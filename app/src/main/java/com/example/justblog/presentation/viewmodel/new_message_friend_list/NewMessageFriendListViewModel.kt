package com.example.justblog.presentation.viewmodel.new_message_friend_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justblog.data.state.FriendListState
import com.example.justblog.domain.use_case.GetFriendListFromFirebaseCase
import com.example.justblog.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewMessageFriendListViewModel : ViewModel(), KoinComponent {
    private val getFriendListUseCase: GetFriendListFromFirebaseCase by inject()
    private val _state = MutableStateFlow(FriendListState())
    val state: StateFlow<FriendListState> = _state

    init {
        getFriendList()
    }

    private fun getFriendList() {
        viewModelScope.launch {
            getFriendListUseCase(userId = FirebaseAuth.getInstance().uid!!).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value =
                            FriendListState(
                                friendList = result.data ?: arrayListOf(),
                                isLoading = false
                            )

                    }

                    is Resource.Error -> {
                        _state.value = FriendListState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {
                        _state.value = FriendListState(isLoading = true)
                    }
                }
            }
        }
    }
}