package com.example.justblog.presentation.viewmodel.send_message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justblog.data.model.MessageType
import com.example.justblog.data.state.BooleanState
import com.example.justblog.data.state.MessageListState
import com.example.justblog.data.state.MessageSendState
import com.example.justblog.domain.use_case.CheckChatRoomCase
import com.example.justblog.domain.use_case.CreateChatRoomCase
import com.example.justblog.domain.use_case.GetMessageListCase
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
    private val checkChatRoomCase: CheckChatRoomCase by inject()
    private val createChatRoomCase: CreateChatRoomCase by inject()
    private val _state = MutableStateFlow(MessageSendState())

    private val _checkChatState = MutableStateFlow(BooleanState())
    val chatState: StateFlow<BooleanState> = _checkChatState

    private val createChatState = MutableStateFlow(BooleanState())
    val _createChatState: StateFlow<BooleanState> = createChatState

    private val _messageState = MutableStateFlow(MessageListState())
    var messageState: StateFlow<MessageListState> = _messageState

    init {
        checkChatRoom(chatRoomIdForString())
        getMessageList(chatRoomIdForString(), usersIds)
    }

    private fun getMessageList(chatRoomId: String, usersIds: List<String>) {
        viewModelScope.launch {
            getMessageListCase(chatRoomId, usersIds).collect { result ->
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

    fun sendMessage(chatRoomId: String, hashMap: HashMap<Any, Any>) {
        viewModelScope.launch {
            sendMessageCase(chatRoomId, hashMap).collect { result ->
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

    fun createChatRoom(chatRoomId: String, hashMap: HashMap<Any, Any>) {
        viewModelScope.launch {
            createChatRoomCase(chatRoomId, hashMap).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        createChatState.value =
                            BooleanState(
                                success = result.data ?: false,
                            )

                    }

                    is Resource.Error -> {
                        createChatState.value = BooleanState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun checkChatRoom(chatRoomId: String) {
        viewModelScope.launch {
            checkChatRoomCase(chatRoomId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _checkChatState.value =
                            BooleanState(
                                success = result.data ?: false,
                            )

                    }

                    is Resource.Error -> {
                        _checkChatState.value = BooleanState(
                            error = result.message ?: "An unexpected error occurred!!"
                        )

                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun chatRoomIdForString(): String {
        return if (usersIds.first().hashCode() < usersIds.last().hashCode()) {
            usersIds.first() + "_" + usersIds.last()
        } else {
            usersIds.last() + "_" + usersIds.first()
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

    fun createChatHashMap(chatRoomId: String, userId: String): HashMap<Any, Any> {
        val charRoomMap = HashMap<Any, Any>()
        charRoomMap["chatRoomId"] = chatRoomId
        charRoomMap["lastMessageSenderId"] = userId
        charRoomMap["lastMessageTimestamp"] = FieldValue.serverTimestamp()
        charRoomMap["users"] = usersIds
        return charRoomMap
    }
}