package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justblog.R
import com.example.justblog.data.model.ChatData
import com.example.justblog.utils.CheckUserId
import com.example.justblog.utils.GetTimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class ChatRecyclerViewAdapter(
    val context: Context,
    private var messageArrayList: ArrayList<ChatData>,
) : RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>() {
    private var onClickItem: ((ChatData) -> Unit)? = null
    private var userId = FirebaseAuth.getInstance().uid
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chats_item_layout, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messageArrayList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener {
            onClickItem!!.invoke(item)
        }
        firebaseFirestore.collection("users")
            .document(item.senderId ?: "").addSnapshotListener { value, error ->
                if (error == null) {
                    Glide.with(holder.circleImageView).load(value?.get("profile_img").toString())
                        .into(holder.circleImageView)
                    holder.userName.text = value?.get("name").toString()
                }
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleImageView: CircleImageView =
            itemView.findViewById(R.id.chat_layout_item_circleimageview)
        val userName: TextView = itemView.findViewById(R.id.chat_layout_item_user_name)
        private val date: TextView = itemView.findViewById(R.id.chat_layout_item_last_message)

        @SuppressLint("SetTextI18n")
        fun bindView(item: ChatData) {
            date.text = GetTimeAgo.getTimeAgo(item.lastMessageTimeStamp.time, context)
        }

    }

    override fun getItemCount(): Int {
        return messageArrayList.size
    }

    fun setOnClickItem(callback: (ChatData) -> Unit) {
        this.onClickItem = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<ChatData>) {
        this.messageArrayList = list
        notifyDataSetChanged()
    }
}