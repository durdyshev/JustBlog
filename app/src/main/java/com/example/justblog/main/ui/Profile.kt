package com.example.justblog.main.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.R
import com.example.justblog.databinding.FragmentProfileBinding
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.adapters.ProfileSelectMenuRecyclerAdapter
import com.example.justblog.main.model.PostData
import com.example.justblog.main.model.ProfileData
import com.example.justblog.main.model.ProfileSelectData
import com.example.justblog.utils.UserCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var view: View
    private lateinit var listOfSelectRecyclerView: ArrayList<ProfileSelectData>
    private lateinit var profileSelectAdapter: ProfileSelectMenuRecyclerAdapter
    private var firebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var userCheck: UserCheck
    private var mAuth: FirebaseAuth? = null
    private lateinit var postDataArrayList: ArrayList<PostData>
    private lateinit var postRecyclerViewAdapter: PostRecyclerViewAdapter
    private var tempPostList = arrayListOf<PostData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        view = binding.root
        initThis()
        initSelectRecyclerView()
        initFirebaseProfileData()
        initRecyclerView()
        return view
    }

    private fun initThis() {
        userCheck = UserCheck(requireContext())
        mAuth = FirebaseAuth.getInstance()
    }

    private fun initFirebaseProfileData() {
        firebaseFirestore.collection("users").document(userCheck.userId()!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val name = it.result.getString("name")
                    val userId = it.result.getString("userId")
                    val userName = it.result.getString("username")
                    val motto = it.result.getString("motto")
                    val imgUrl = it.result.getString("profile_img")
                    val profileData = ProfileData(
                        name = name!!,
                        username = userName!!,
                        motto = motto!!,
                        profileImg = imgUrl!!,
                        userId = userId!!
                    )
                    binding.profileInfo = profileData
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

        if (mAuth!!.currentUser != null) {
            firebaseFirestore.collection("/users/${userCheck.userId()}/posts/").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (documentSnapshot in it.result) {
                            val postId = documentSnapshot.id
                            val compUrl = documentSnapshot.getString("comp_url")
                            val description = documentSnapshot.getString("description")
                            val imageUrl = documentSnapshot.getString("image_url")
                            val type = documentSnapshot.getString("type")
                            val userId = documentSnapshot.getString("user_id")
                            val date = documentSnapshot.getTimestamp("date")
                            val postData =
                                PostData(
                                    postId,
                                    compUrl,
                                    description,
                                    imageUrl,
                                    type,
                                    userId,
                                    date!!.toDate()
                                )
                            postDataArrayList.add(postData)
                        }

                        val newList = postDataArrayList.sortedWith(compareBy { it.date }).reversed()
                        val newArrayList = java.util.ArrayList<PostData>()
                        newArrayList.addAll(newList)
                        postRecyclerViewAdapter =
                            PostRecyclerViewAdapter(requireContext(), newArrayList)
                        binding.fragmentProfileProfilePostRecyclerview.layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                        binding.fragmentProfileProfilePostRecyclerview.adapter =
                            postRecyclerViewAdapter

                    }
                }
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