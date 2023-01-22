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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postDataArrayList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(item)
        }

        firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Like")
            .document(userCheck.userId()!!).get().addOnCompleteListener { task ->
            if (task.result.exists()) {
                holder.likeImageView.setImageResource(R.drawable.up_arrow_icon_blue)
            } else {
                holder.likeImageView.setImageResource(R.drawable.up_arrow_icon)
            }
        }

        firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Dislike")
            .document(userCheck.userId()!!).get().addOnCompleteListener { task ->
                if (task.result.exists()) {
                    holder.dislikeImageView.setImageResource(R.drawable.down_arrow_icon_blue)
                } else {
                    holder.dislikeImageView.setImageResource(R.drawable.down_arrow_icon)
                }
            }

        holder.likeImageView.setOnClickListener {
            firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Like")
                .document(userCheck.userId()!!).get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        val likeMap: MutableMap<String, Any> = HashMap()
                        likeMap["date"] = FieldValue.serverTimestamp()
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Like").document(userCheck.userId()!!).set(likeMap)
                        holder.likeImageView.setImageResource(R.drawable.up_arrow_icon_blue)

                    } else {
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Like").document(userCheck.userId()!!).delete()
                        holder.likeImageView.setImageResource(R.drawable.up_arrow_icon)


                    }
                }
        }

        holder.dislikeImageView.setOnClickListener {
            firebaseFirestore.collection("/users/${item.user_id}/posts/${item.postId}/Dislike")
                .document(userCheck.userId()!!).get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        val likeMap: MutableMap<String, Any> = HashMap()
                        likeMap["date"] = FieldValue.serverTimestamp()
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Dislike").document(userCheck.userId()!!).set(likeMap)
                        holder.dislikeImageView.setImageResource(R.drawable.down_arrow_icon_blue)
                    } else {
                        firebaseFirestore.collection("/users/${item.user_id}/posts")
                            .document(item.postId)
                            .collection("Dislike").document(userCheck.userId()!!).delete()
                        holder.dislikeImageView.setImageResource(R.drawable.down_arrow_icon)
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
        var dislikeImageView: ImageView = itemView.findViewById(R.id.post_layout_item_dislike)
        var commentImageView: ImageView = itemView.findViewById(R.id.post_layout_item_comment)
        var shareImageView: ImageView = itemView.findViewById(R.id.post_layout_item_share)

        @SuppressLint("SetTextI18n")
        fun bindView(item: PostData) {

            firebaseFirestore.collection("users").document(item.user_id!!).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val name = it.getString("name")
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