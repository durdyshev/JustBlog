package com.example.justblog.presentation.viewmodel.send_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justblog.data.model.MessageType
import com.example.justblog.data.state.MessageListState
import com.example.justblog.data.state.MessageSendState
import com.example.justblog.data.state.ProfileState
import com.example.justblog.domain.use_case.GetMessageListCase
import com.example.justblog.domain.use_case.GetProfileCase
import com.example.justblog.domain.use_case.SendMessageCase
import com.example.justblog.utils.Resource
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SendMessageViewModel(private val usersIds: List<String>) : ViewModel(), KoinComponent {
    private val sendMessageCase: SendMessageCase by inject()
    private val getMessageListCase: GetMessageListCase by inject()
    private val getProfileCase: GetProfileCase by inject()

    private val _state = MutableStateFlow(MessageSendState())
    val state: StateFlow<MessageSendState> = _state

    private val _messageState = MutableStateFlow(MessageListState())
    var messageState: StateFlow<MessageListState> = _messageState

    private val friendProfile = MutableStateFlow(ProfileState())
    var _friendProfile: StateFlow<ProfileState> = friendProfile

    init {
        getMessageList()
        getFriendProfile()
    }

    private fun getMessageList() {
        viewModelScope.launch {
            getMessageListCase(chatRoomId(), usersIds).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _messageState.value =
                            MessageListState(
                                messageList = result.data ?: arrayListOf(),
                                isLoading = false
                            )

                    }

                    is Resource.Error -> {
                        _messageState.value = MessageListState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {
                        _messageState.value = MessageListState(isLoading = true)
                    }
                }
            }
        }
    }

    private fun getFriendProfile() {
        viewModelScope.launch {
            getProfileCase(usersIds[1]).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        friendProfile.value =
                            ProfileState(
                                success = true,
                                profileData = result.data,
                            )

                    }

                    is Resource.Error -> {
                        friendProfile.value = ProfileState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {
                        friendProfile.value = ProfileState(isLoading = true)
                    }
                }
            }
        }
    }

    fun sendMessage(hashMap: HashMap<Any, Any>) {
        viewModelScope.launch {
            sendMessageCase(chatRoomId(), hashMap).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value =
                            MessageSendState(
                                success = result.data ?: false,
                            )

                    }

                    is Resource.Error -> {
                        _state.value = MessageSendState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun chatRoomId(): String {
        return if (usersIds[0].hashCode() < usersIds[1].hashCode()) {
            usersIds[0] + "_" + usersIds[1]
        } else {
            usersIds[1] + "_" + usersIds[0]
        }
    }

    fun createHashMap(uid: String?, friendId: String, content: String): HashMap<Any, Any> {
        val messageHashMap = HashMap<Any, Any>()
        messageHashMap["senderId"] = uid ?: ""
        messageHashMap["receiverId"] = friendId
        messageHashMap["content"] = content
        messageHashMap["date"] = FieldValue.serverTimestamp()
        messageHashMap["users"] = arrayListOf(uid, friendId)
        messageHashMap["isRead"] = false
        messageHashMap["type"] = MessageType.TEXT.name
        return messageHashMap
    }
}