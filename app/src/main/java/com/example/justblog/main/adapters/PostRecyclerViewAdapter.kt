package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justblog.R
import com.example.justblog.main.model.PostData
import com.example.justblog.utils.UserCheck
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
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

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_layout_item, parent, false)
        return ViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postDataArrayList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(item)
        }

        firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Likes")
            .addSnapshotListener { value, _ ->
                if (value!!.size() != 0) {
                    holder.likeTextView.visibility = View.VISIBLE
                    holder.likeTextView.text = "Likes:${value.size()}"
                } else {
                    holder.likeTextView.visibility = View.GONE
                }
            }

        firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Comments")
            .addSnapshotListener { value, _ ->
                if (value!!.size() != 0) {
                    holder.commentTextView.visibility = View.VISIBLE
                    holder.commentTextView.text = "Comments:${value.size()}"
                } else {
                    holder.commentTextView.visibility = View.GONE
                }
            }

        firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Likes")
            .document(userCheck.userId()!!).get().addOnCompleteListener { task ->
                if (task.result.exists()) {
                    holder.likeImageView.setImageResource(R.drawable.baseline_favorite_24)
                } else {
                    holder.likeImageView.setImageResource(R.drawable.baseline_favorite_border_24)
                }
            }

        holder.likeImageView.setOnClickListener {
            firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Likes")
                .document(userCheck.userId()!!).get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        val likeMap: MutableMap<String, Any> = HashMap()
                        likeMap["date"] = FieldValue.serverTimestamp()
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Like").document(userCheck.userId()!!).set(likeMap)
                        holder.likeImageView.setImageResource(R.drawable.baseline_favorite_24)

                    } else {
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Like").document(userCheck.userId()!!).delete()
                        holder.likeImageView.setImageResource(R.drawable.baseline_favorite_border_24)

                    }
                }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imageViewUserShow: ImageView =
            itemView.findViewById(R.id.post_layout_item_imageview)
        private var descTextview: TextView = itemView.findViewById(R.id.post_layout_item_desc)
        private var dateTextview: TextView = itemView.findViewById(R.id.post_layout_item_date)
        private var nameTextView: TextView = itemView.findViewById(R.id.post_layout_item_name)
        var likeImageView: ImageView = itemView.findViewById(R.id.post_layout_item_like)
        var commentImageView: ImageView = itemView.findViewById(R.id.post_layout_item_comment)
        var shareImageView: ImageView = itemView.findViewById(R.id.post_layout_item_share)
        var likeTextView: TextView = itemView.findViewById(R.id.post_layout_item_likes)
        var commentTextView: TextView = itemView.findViewById(R.id.post_layout_item_comments)

        @SuppressLint("SetTextI18n")
        fun bindView(item: PostData) {

            firebaseFirestore.collection("users").document(item.user_id!!).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val name = it.result.getString("name")
                        nameTextView.text = name
                    }
                }

            descTextview.text = item.description
            Glide.with(context).load(item.comp_url).into(imageViewUserShow)
            val dateString =
                DateFormat.format("dd/MM/yyyy hh:mm", Date(item.date!!.time)).toString()
            dateTextview.text = dateString


        }
    }

    override fun getItemCount(): Int {
        return postDataArrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: ArrayList<PostData>) {
        this.postDataArrayList = items
        notifyDataSetChanged()
    }

    fun deleteItem(userData: PostData) {
        val index = this.postDataArrayList.indexOf(userData)
        this.postDataArrayList.removeAt(index)
        this.notifyItemRemoved(index)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updateList: ArrayList<PostData>) {
        this.postDataArrayList = updateList
        notifyDataSetChanged()
    }
}