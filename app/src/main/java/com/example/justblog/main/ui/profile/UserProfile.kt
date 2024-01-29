package com.example.justblog.main.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.R
import com.example.justblog.databinding.FragmentUserProfileBinding
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.adapters.ProfileSelectMenuRecyclerAdapter
import com.example.justblog.main.model.PostData
import com.example.justblog.main.model.ProfileData
import com.example.justblog.main.model.ProfileSelectData
import com.example.justblog.main.viewmodel.UserProfileViewModel
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.FirebaseFirestore


class UserProfile : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var view: View
    private lateinit var profileData: ProfileData
    private lateinit var profileSelectAdapter: ProfileSelectMenuRecyclerAdapter
    private lateinit var listOfSelectRecyclerView: ArrayList<ProfileSelectData>
    private lateinit var postDataArrayList: ArrayList<PostData>
    private lateinit var postRecyclerViewAdapter: PostRecyclerViewAdapter
    private var tempPostList = arrayListOf<PostData>()
    private var firebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userCheck: UserCheck
    private lateinit var userProfileViewModel: UserProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        initThis()

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(layoutInflater, container, false)
        binding.profileInfo = profileData
        initClickListeners()
        initRecyclerView()
        initSelectRecyclerView()
        view = binding.root
        return view
    }

    private fun initThis() {
        userCheck = UserCheck(requireContext())
        profileData = requireArguments().get("profileData") as ProfileData

        if (userCheck.userId() == profileData.userId) {
            binding.userProfileAddFriendHeaderLinear.visibility = View.GONE
        } else {
            userProfileViewModel.friendExistenceCheck(userCheck, profileData)
            userProfileViewModel.statusString.observe(this) {
                binding.userProfileAddFriendTextview.text = it
            }
            userProfileViewModel.imageValue.observe(this) {
                binding.userProfileAddFriendLinear.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        it
                    )
            }
        }
    }

    private fun initClickListeners() {
        binding.userProfileAddFriendLinear.setOnClickListener {
            userProfileViewModel.userProfileClick(userCheck, profileData, requireContext())
            userProfileViewModel.buttonEnable.observe(viewLifecycleOwner) {
                binding.userProfileAddFriendLinear.isEnabled = it
            }
        }
    }


    private fun initSelectRecyclerView() {
        val item1 = ProfileSelectData("Posts")
        val item2 = ProfileSelectData("Photos")
        val item3 = ProfileSelectData("Videos")

        listOfSelectRecyclerView = ArrayList()
        listOfSelectRecyclerView.add(item1)
        listOfSelectRecyclerView.add(item2)
        listOfSelectRecyclerView.add(item3)

        profileSelectAdapter =
            ProfileSelectMenuRecyclerAdapter(requireContext(), listOfSelectRecyclerView)
        binding.fragmentProfileProfilePostSelectRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentProfileProfilePostSelectRecyclerview.adapter = profileSelectAdapter

        profileSelectAdapter.setOnClickListener(object :
            ProfileSelectMenuRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                linearlayout: LinearLayout,
                textview: TextView,
                linearlayoutList: ArrayList<LinearLayout>
            ) {
                var i = 0
                while (i < linearlayoutList.size) {
                    linearlayoutList[i].background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_main_button_background
                    )
                    i++
                }
                linearlayoutList[position].background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                )

                when (position) {
                    0 -> {
                        val newList = postDataArrayList.sortedWith(compareBy { it.date }).reversed()
                        val newArrayList = java.util.ArrayList<PostData>()
                        newArrayList.addAll(newList)
                        postRecyclerViewAdapter.updateItems(newArrayList)
                    }

                    1 -> {
                        postRecyclerViewAdapter.updateItems(sortList("photo"))
                    }

                    2 -> {
                        postRecyclerViewAdapter.updateItems(sortList("video"))
                    }
                }
            }
        })
    }

    private fun initRecyclerView() {

        postDataArrayList = ArrayList()
        firebaseFirestore = FirebaseFirestore.getInstance()

        postRecyclerViewAdapter = PostRecyclerViewAdapter(requireContext(), postDataArrayList)
        binding.fragmentProfileProfilePostRecyclerview.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.fragmentProfileProfilePostRecyclerview.adapter = postRecyclerViewAdapter

        userProfileViewModel.getUserPosts(profileData)
        userProfileViewModel.postDataArrayList.observe(viewLifecycleOwner) {
            postRecyclerViewAdapter.updateItems(it)
        }
    }
    fun sortList(type: String): ArrayList<PostData> {
        tempPostList.clear()
        for (hey in postDataArrayList) {
            if (hey.type == type) {
                tempPostList.add(hey)
            }
        }
        return tempPostList

    }
}