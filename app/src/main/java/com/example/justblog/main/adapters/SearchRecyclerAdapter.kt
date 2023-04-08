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
import com.example.justblog.main.model.ProfileData
import com.google.firebase.firestore.*
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class SearchRecyclerAdapter(
    val context: Context,
    private var postDataArrayList: List<ProfileData>,
) : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>() {
    private var onClickItem: ((ProfileData)->Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_layout_item, parent, false)
        return ViewHolder(v)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postDataArrayList[position]
        holder.bindView(item)
        holder.itemView.setOnClickListener {
            onClickItem!!.invoke(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val circleImageView:CircleImageView=itemView.findViewById(R.id.search_layout_item_circleimageview)
        private val name:TextView=itemView.findViewById(R.id.search_layout_item_name)
        private val userName:TextView=itemView.findViewById(R.id.search_layout_item_username)
        @SuppressLint("SetTextI18n")
        fun bindView(item: ProfileData) {
            name.text=item.name
            userName.text=item.username
            Glide.with(context).load(item.profileImg).into(circleImageView)
        }

    }

    override fun getItemCount(): Int {
        return postDataArrayList.size
    }
    fun setOnClickItem(callback: (ProfileData)->Unit){
        this.onClickItem = callback
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ProfileData>){
        this.postDataArrayList=list
        notifyDataSetChanged()
    }
}