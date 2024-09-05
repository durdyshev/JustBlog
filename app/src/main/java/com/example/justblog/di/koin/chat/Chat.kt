package com.example.justblog.di.koin.chat

import com.example.justblog.data.repository.chat.ChatListRepositoryImpl
import com.example.justblog.data.service.firebase.chat.ChatListService
import com.example.justblog.domain.repository.chat.ChatListRepository
import com.example.justblog.domain.use_case.GetChatListCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object Chat {
    val appModule = module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        singleOf(::ChatListService) { bind<ChatListService>() }
        singleOf(::ChatListRepositoryImpl) { bind<ChatListRepository>() }
        singleOf(::GetChatListCase) { bind<GetChatListCase>() }
    }
}