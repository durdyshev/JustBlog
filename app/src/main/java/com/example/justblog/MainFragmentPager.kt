package com.example.justblog


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.justblog.main.ui.addpost.AddPost
import com.example.justblog.main.ui.chat.ChatParent
import com.example.justblog.main.ui.home.HomeParent

private const val NUM_TABS = 3


class MainFragmentPager(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AddPost()
            1 -> return HomeParent()
            2 -> return ChatParent()
        }
        return HomeParent()
    }
}