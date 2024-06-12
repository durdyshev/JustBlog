package com.example.justblog.main.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.R
import com.example.justblog.databinding.FragmentUserProfileBinding
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.adapters.ProfileSelectMenuRecyclerAdapter
import com.example.justblog.main.model.PostData
import com.example.justblog.main.model.ProfileData
import com.example.justblog.main.model.ProfileSelectData
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap


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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val userExistenceInFriends=firebaseFirestore.collection("/users/${userCheck.userId()}/friends")
            .document(profileData.userId!!)
        userExistenceInFriends.get().addOnCompleteListener {
            if (it.result.exists()) {
                when (it.result.getString("status")) {
                    "0" -> {
                        binding.userProfileAddFriendTextview.text = "Send Request"
                        binding.userProfileAddFriendLinear.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_main_button_background
                            )
                    }
                    "1" -> {
                        binding.userProfileAddFriendTextview.text = "Friend Request"
                        binding.userProfileAddFriendLinear.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_main_button_background
                            )
                    }
                    "2"->{
                        binding.userProfileAddFriendTextview.text = "Friend"
                        binding.userProfileAddFriendLinear.background =
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                            )
                    }
                }
            } else if (userCheck.userId() == profileData.userId) {
                binding.userProfileAddFriendHeaderLinear.visibility = View.GONE
            } else {
                binding.userProfileAddFriendTextview.text = "Add Friend"
                binding.userProfileAddFriendLinear.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                    )
            }
        }
    }

    private fun initClickListeners() {
        binding.userProfileAddFriendLinear.setOnClickListener {
            binding.userProfileAddFriendLinear.isEnabled = false
            val userExistenceInFriends= firebaseFirestore.collection("/users/${userCheck.userId()}/friends")
                .document(profileData.userId!!)
            userExistenceInFriends.get().addOnCompleteListener {
                if (!it.result.exists()) {
                    //If user is not your friend
                    val firstFriendMap: MutableMap<String, Any> = HashMap()
                    firstFriendMap["date"] = FieldValue.serverTimestamp()
                    firstFriendMap["status"] = "0"
                    val secondFriendMap: MutableMap<String, Any> = HashMap()
                    secondFriendMap["date"] = FieldValue.serverTimestamp()
                    secondFriendMap["status"] = "1"
                    userExistenceInFriends.set(firstFriendMap)
                    firebaseFirestore.collection("/users/${profileData.userId}/friends")
                        .document(userCheck.userId()!!).set(secondFriendMap).addOnCompleteListener {
                            if (it.isSuccessful) {
                                binding.userProfileAddFriendLinear.background =
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_main_button_background
                                    )
                                binding.userProfileAddFriendTextview.text = "Friend Request"
                                binding.userProfileAddFriendLinear.isEnabled = true
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Problem occurred:${it.exception}",
                                    Toast.LENGTH_LONG
                                ).show()
                                binding.userProfileAddFriendLinear.isEnabled = true
                            }
                        }
                } else {
                    //If user is your friend
                    val friendStatus=it.result.get("status")
                    when(friendStatus){
                        "0"->{

                        }
                        "1"->{

                        }
                    }
                    userExistenceInFriends.delete()
                    firebaseFirestore.collection("/users/${profileData.userId}/friends")
                        .document(userCheck.userId()!!).delete().addOnCompleteListener {

                            binding.userProfileAddFriendTextview.text = "Add Friend"
                            binding.userProfileAddFriendLinear.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_arena_recyclerview_linear_layout_textview_background
                                )
                            binding.userProfileAddFriendLinear.isEnabled = true

                        }
                }
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

        firebaseFirestore.collection("/users/${profileData.userId}/posts/").get()
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