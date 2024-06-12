package com.example.justblog

import android.app.Application
import com.example.justblog.di.koin.get_friend_list.GetFriendListModule
import com.example.justblog.di.koin.send_message.SendMessageModule
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(GetFriendListModule.appModule)
            modules(SendMessageModule.appModule)
        }
    }
}