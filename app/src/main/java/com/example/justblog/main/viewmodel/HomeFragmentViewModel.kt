package com.example.justblog.main.viewmodel

import android.app.Application
import com.example.justblog.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HomeFragmentViewModel(application: Application) : BaseViewModel(application), CoroutineScope {
    val app=application
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

}