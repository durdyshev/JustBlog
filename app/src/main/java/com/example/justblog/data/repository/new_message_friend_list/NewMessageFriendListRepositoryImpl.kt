package com.example.justblog.data.repository.new_message_friend_list

import com.example.justblog.data.service.firebase.new_message_friend_list.NewMessageFriendListService
import com.example.justblog.domain.repository.new_message_friend_list_repository.NewMessageFriendListRepository
import com.google.firebase.firestore.CollectionReference

class NewMessageFriendListRepositoryImpl(
    private val newMessageFriendListService: NewMessageFriendListService
) :
    NewMessageFriendListRepository {
    override suspend fun getFriendList(userId: String): CollectionReference {
        return newMessageFriendListService.getFriendList(userId)
    }
}