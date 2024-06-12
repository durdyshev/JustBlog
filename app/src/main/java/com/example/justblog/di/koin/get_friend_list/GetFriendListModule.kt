package com.example.justblog.di.koin.get_friend_list

import com.example.justblog.data.repository.new_message_friend_list.NewMessageFriendListRepositoryImpl
import com.example.justblog.data.service.firebase.new_message_friend_list.NewMessageFriendListService
import com.example.justblog.domain.repository.new_message_friend_list_repository.NewMessageFriendListRepository
import com.example.justblog.domain.use_case.GetFriendListFromFirebaseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object GetFriendListModule {
    val appModule = module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        singleOf(::NewMessageFriendListService) { bind<NewMessageFriendListService>() }
        singleOf(::NewMessageFriendListRepositoryImpl) { bind<NewMessageFriendListRepository>() }
        singleOf(::GetFriendListFromFirebaseCase) { bind<GetFriendListFromFirebaseCase>() }
    }
}