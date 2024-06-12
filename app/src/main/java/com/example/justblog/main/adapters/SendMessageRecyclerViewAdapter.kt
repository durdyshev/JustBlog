package com.example.justblog.main.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.justblog.R
import com.example.justblog.data.model.MessageData
import de.hdodenhof.circleimageview.CircleImageView

class SendMessageRecyclerViewAdapter(
    val context: Context,
    private var messageArrayList: ArrayList<MessageData>,
) : RecyclerView.Adapter<SendMessageRecyclerViewAdapter.ViewHolder>() {
    private var onClickItem: ((MessageData) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.send_message_layout_item, parent, false)
        return ViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messageArrayList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener {
            onClickItem!!.invoke(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val circleImageView: CircleImageView =
            itemView.findViewById(R.id.message_layout_item_circleimageview)
        private val message: TextView = itemView.findViewById(R.id.message_layout_item_message)

        @SuppressLint("SetTextI18n")
        fun bindView(item: MessageData) {
            message.text = item.content
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
}