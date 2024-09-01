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
import com.example.justblog.data.model.MessageData
import com.example.justblog.utils.GetTimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class SendMessageRecyclerViewAdapter(
    val context: Context,
    private var messageArrayList: ArrayList<MessageData>,
) : RecyclerView.Adapter<SendMessageRecyclerViewAdapter.ViewHolder>() {
    private var onClickItem: ((MessageData) -> Unit)? = null
    private var userId = FirebaseAuth.getInstance().uid
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            if (viewType == MSG_TYPE_RIGHT) {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_message_layout_item_right, parent, false)
            } else {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_message_layout_item_left, parent, false)
            }
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
            .document(item.senderId).addSnapshotListener { value, error ->
                if (error == null) {
                    Glide.with(holder.circleImageView).load(value?.get("profile_img").toString())
                        .into(holder.circleImageView)
                }
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleImageView: CircleImageView =
            itemView.findViewById(R.id.message_layout_item_circleimageview)
        private val message: TextView = itemView.findViewById(R.id.message_layout_item_message)
        private val date: TextView = itemView.findViewById(R.id.message_layout_item_date)

        @SuppressLint("SetTextI18n")
        fun bindView(item: MessageData) {
            message.text = item.content
            date.text = GetTimeAgo.getTimeAgo(item.date.time, context)
        }

    }

    override fun getItemCount(): Int {
        return messageArrayList.size
    }

    fun setOnClickItem(callback: (MessageData) -> Unit) {
        this.onClickItem = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<MessageData>) {
        this.messageArrayList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageArrayList[position].senderId == userId) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}