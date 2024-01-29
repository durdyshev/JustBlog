package com.example.justblog.main.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justblog.databinding.FragmentCommentsBinding
import com.example.justblog.main.adapters.CommentsRecyclerViewAdapter
import com.example.justblog.main.adapters.PostRecyclerViewAdapter
import com.example.justblog.main.model.CommentData
import com.example.justblog.main.viewmodel.CommentsViewModel
import com.example.justblog.main.viewmodel.HomeFragmentViewModel
import com.example.justblog.utils.UserCheck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Comments : Fragment() {
    private lateinit var binding:FragmentCommentsBinding
    private lateinit var commentsViewModel:CommentsViewModel
    private lateinit var commentsRecyclerViewAdapter: CommentsRecyclerViewAdapter
    private val commentArrayList=ArrayList<CommentData>()
    private var postId:String=""
    private lateinit var userCheck: UserCheck
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCommentsBinding.inflate(layoutInflater, container, false)
        initPostRecyclerView()
        initThis()
        initClickListeners()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initClickListeners() {
        binding.commentsPostBtn.setOnClickListener {
            commentsViewModel.addComment(postId,binding.commentsEnterEdittext.text.toString(),userCheck)
        }
    }

    private fun initPostRecyclerView() {
        commentsRecyclerViewAdapter = CommentsRecyclerViewAdapter(requireContext(), commentArrayList)
        binding.commentsRecyclerview.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        binding.commentsRecyclerview.adapter = commentsRecyclerViewAdapter
    }

    private fun initThis() {
        userCheck=UserCheck(requireContext())
        postId = requireArguments().get("postId") as String
        commentsViewModel = ViewModelProvider(this)[CommentsViewModel::class.java]
        CoroutineScope(Dispatchers.Main).launch {
        commentsViewModel.getPostCommentsByPostId(postId)
        }
        commentsViewModel.commentDataList.observe(viewLifecycleOwner){
            Log.e("boy",it.size.toString())
            commentsRecyclerViewAdapter.updateItems(it)
        }
    }
}