package com.example.justblog.di.koin.send_message

import com.example.justblog.data.repository.send_message.SendMessageRepositoryImpl
import com.example.justblog.data.service.firebase.send_message.SendMessageService
import com.example.justblog.domain.repository.send_message.SendMessageRepository
import com.example.justblog.domain.use_case.GetMessageListCase
import com.example.justblog.domain.use_case.SendMessageCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object SendMessageModule {
    val appModule = module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        singleOf(::SendMessageService) { bind<SendMessageService>() }
        singleOf(::SendMessageRepositoryImpl) { bind<SendMessageRepository>() }
        singleOf(::SendMessageCase) { bind<SendMessageCase>() }
        singleOf(::GetMessageListCase) { bind<GetMessageListCase>() }

    }
}