package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.justblog.R
import com.example.justblog.databinding.PostLayoutItemBinding
import com.example.justblog.main.model.PostData
import com.example.justblog.main.ui.home.HomeParent.Companion.navController
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class PostRecyclerViewAdapter(
    val context: Context,
    private var postDataArrayList: ArrayList<PostData>,
) : RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>() {
    private var onClickItem: ((PostData) -> Unit)? = null
    private var firebaseFirestore = FirebaseFirestore.getInstance()
    private var userCheck = UserCheck(context)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        /*  val v = LayoutInflater.from(parent.context)
              .inflate(R.layout.post_layout_item, parent, false)
          return ViewHolder(v)*/

        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<PostLayoutItemBinding>(
            inflater,
            R.layout.post_layout_item, parent, false
        )
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postDataArrayList[position]
        CoroutineScope(Dispatchers.IO).launch {
            async {
                firebaseFirestore.collection("users")
                    .document(item.user_id!!).addSnapshotListener { value, error ->
                        if (error == null) {
                            val name = value?.get("name").toString()
                            val imgUrl = value?.get("profile_img").toString()
                            holder.view.name = name
                            holder.view.profileImg = imgUrl
                        }

                    }
            }.await()
            holder.view.compImg = item.comp_url
            holder.view.postDate = item.date
            holder.view.desc = item.description

            firebaseFirestore.collection("posts/${item.postId}/likes")
                .addSnapshotListener { value, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        holder.view.likesCount = value!!.size()
                    }
                }

            firebaseFirestore.collection("posts/${item.postId}/comments")
                .addSnapshotListener { value, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        holder.view.commentsCount = value!!.size()
                    }
                }

            firebaseFirestore.collection("posts/${item.postId}/likes")
                .document(userCheck.userId()!!).get().addOnCompleteListener { task ->
                    CoroutineScope(Dispatchers.Main).launch {
                        holder.view.heartBoolean = task.result.exists()
                    }
                }
        }
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(item)
        }
        holder.view.postLayoutItemComment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("postId", item.postId)
            navController.navigate(R.id.comments, bundle)
        }
        holder.view.postLayoutItemLike.setOnClickListener {
            firebaseFirestore.collection("posts/${item.postId}/likes")
                .document(userCheck.userId()!!).get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        val likeMap: MutableMap<String, Any> = HashMap()
                        likeMap["date"] = FieldValue.serverTimestamp()
                        firebaseFirestore.collection("posts")
                            .document(item.postId)
                            .collection("likes").document(userCheck.userId()!!).set(likeMap)
                        CoroutineScope(Dispatchers.Main).launch {
                            holder.view.postLayoutItemLike.setImageResource(R.drawable.baseline_favorite_24)
                        }
                    } else {
                        firebaseFirestore.collection("posts")
                            .document(item.postId)
                            .collection("likes").document(userCheck.userId()!!).delete()
                        CoroutineScope(Dispatchers.Main).launch {
                            holder.view.postLayoutItemLike.setImageResource(R.drawable.baseline_favorite_border_24)
                        }

                    }
                }
        }

    }

    inner class ViewHolder(var view: PostLayoutItemBinding) : RecyclerView.ViewHolder(view.root) {

    }

    override fun getItemCount(): Int {
        return postDataArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: ArrayList<PostData>) {
        this.postDataArrayList = items
        notifyDataSetChanged()
    }
}
