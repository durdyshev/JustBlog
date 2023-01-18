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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PostRecyclerViewAdapter(

    val context: Context,
    var postDataArrayList: ArrayList<PostData>,
) : RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder>() {
     private var onClickItem: ((PostData) -> Unit)? = null
     private var firebaseFirestore=FirebaseFirestore.getInstance()
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
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         private var imageViewUserShow: ImageView = itemView.findViewById(R.id.post_layout_item_imageview)
         private var descTextview: TextView = itemView.findViewById(R.id.post_layout_item_desc)
         private var dateTextview: TextView = itemView.findViewById(R.id.post_layout_item_date)
         private var nameTextView: TextView = itemView.findViewById(R.id.post_layout_item_name)

        @SuppressLint("SetTextI18n")
        fun bindView(item: PostData) {

        firebaseFirestore.collection("users").document(item.user_id!!).get().addOnSuccessListener {
            if (it.exists()){
                val name=it.getString("name")
                nameTextView.text=name
            }
        }
        descTextview.text= item.description
        Glide.with(context).load(item.comp_url).into(imageViewUserShow)
        val dateString=DateFormat.format("dd/MM/yyyy hh:mm", Date(item.date!!.time)).toString()
        dateTextview.text= dateString



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
    fun updateList(updateList: ArrayList<PostData>){
        this.postDataArrayList=updateList
        notifyDataSetChanged()
    }
}