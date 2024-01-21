package com.example.justblog.main.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.R
import com.example.justblog.databinding.FragmentSearchBinding
import com.example.justblog.main.adapters.SearchRecyclerAdapter
import com.example.justblog.main.model.ProfileData
import com.google.firebase.firestore.FirebaseFirestore


class Search : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var view: View
    private var firebaseFirestore = FirebaseFirestore.getInstance()
    private var searchList = ArrayList<ProfileData>()
    private lateinit var searchRecyclerAdapter: SearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        searchView()
        initRecyclerView()
        view = binding.root
        return view
    }

    private fun initRecyclerView() {
        searchRecyclerAdapter =
            SearchRecyclerAdapter(requireContext(), searchList)
        binding.searchRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchRecyclerview.adapter = searchRecyclerAdapter

        searchRecyclerAdapter.setOnClickItem {

            val bundle = Bundle()
            bundle.putParcelable("profileData", it)
            MainActivity.navController.navigate(R.id.userProfile, bundle)
        }
    }

    private fun searchView() {

        binding.layoutSearchview.setOnClickListener {
            binding.loginSearchView.onActionViewExpanded()
        }

        binding.loginSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {

                if (p0.isEmpty()) {
                    searchList.clear()
                    searchRecyclerAdapter.updateList(searchList)
                } else {
                    search(p0)
                }
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                if (p0.isEmpty()) {
                    searchList.clear()
                    searchRecyclerAdapter.updateList(searchList)
                } else {
                    search(p0)
                }
                return true
            }
        })
    }

    private fun search(p0: String) {
        searchList.clear()

        firebaseFirestore.collection("users").orderBy("lower_username").startAt(p0)
            .endAt(p0 + "\uf8ff").addSnapshotListener { value, error ->

                if (error != null) {
                    Log.w("qwerty", "listen:error", error)
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    val name = doc.getString("name")
                    val username = doc.getString("username")
                    val profileImg = doc.getString("profile_img")
                    val userId = doc.getString("userId")
                    val motto = doc.getString("motto")
                    val profileData = ProfileData(
                        name = name,
                        username = username,
                        profileImg = profileImg,
                        userId = userId,
                        motto = motto
                    )
                    if (!searchList.contains(profileData)) {
                        searchList.add(profileData)
                    }

                }

                searchRecyclerAdapter.updateList(searchList)

            }

        /* firebaseFirestore.collection("users").orderBy("username").startAt(searchText)
             .endAt(searchText+"\uf8ff").addSnapshotListener { value, error ->
                 if(!value!!.isEmpty) {
                     for (doc in value) {
                         val name = doc.getString("name")
                         val username = doc.getString("username")
                         val profileImg = doc.getString("profile_img")
                         val userId = doc.getString("userId")
                         val profileData = ProfileData(
                             name = name,
                             username = username,
                             profileImg = profileImg,
                             userId = userId
                         )
                         if (!searchList.contains(profileData)) {
                             searchList.add(profileData)
                         }
                     }
                     Log.e("qwertyiki",searchList.size.toString())


                 }
             }*/
        /*for (hey in searchList){
            Log.e("qwerty",hey.name!!)
            Log.e("qwerty",hey.username!!)
        }*/
    }
}