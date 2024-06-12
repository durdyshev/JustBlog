package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.justblog.R
import com.example.justblog.databinding.CommentLayoutItemBinding
import com.example.justblog.main.model.CommentData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CommentsRecyclerViewAdapter(
    val context: Context,
    private var commentArrayList: ArrayList<CommentData>,
) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {
    private var firebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<CommentLayoutItemBinding>(
            inflater,
            R.layout.comment_layout_item, parent, false
        )
        return ViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = commentArrayList[position]
        holder.view.comment = item.comment

        CoroutineScope(Dispatchers.IO).launch {
            async {
                firebaseFirestore.collection("users")
                    .document(item.userId).addSnapshotListener { value, error ->
                        if (error == null) {
                            val name = value?.get("name").toString()
                            val imgUrl = value?.get("profile_img").toString()
                            holder.view.name = name
                            holder.view.profileImg = imgUrl
                        }

                    }
            }.await()
        }
    }

    inner class ViewHolder(var view: CommentLayoutItemBinding) :
        RecyclerView.ViewHolder(view.root) {

    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: ArrayList<CommentData>) {
        this.commentArrayList = items
        notifyDataSetChanged()
    }
}