package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.justblog.R
import com.example.justblog.data.model.FriendData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfileListRecyclerViewAdapter(
    val context: Context,
    private var profileArrayList: ArrayList<FriendData>,
) : RecyclerView.Adapter<ProfileListRecyclerViewAdapter.ViewHolder>() {
    private var setOnClickItem: ((FriendData) -> Unit)? = null
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_layout_item, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            setOnClickItem?.invoke(profileArrayList[position])
        }
       // Glide.with(context).load(profileArrayList[position]).into(holder.imageViewUserImage)
        CoroutineScope(Dispatchers.IO).launch {
            async {
                firebaseFirestore.collection("users")
                    .document(profileArrayList[position].friendId)
                    .addSnapshotListener { value, error ->
                        if (error == null) {
                            val name = value?.get("name").toString()
                            val userName = value?.get("username").toString()
                            val imgUrl = value?.get("profile_img").toString()
                            holder.name.text = name
                            holder.username.text = userName
                            Glide.with(context).load(imgUrl).into(holder.imageViewUserImage)
                        }

                    }
            }.await()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewUserImage: ImageView =
            itemView.findViewById(R.id.profile_layout_item_circle_imageview)
        val name: TextView = itemView.findViewById(R.id.profile_layout_item_name)
        val username: TextView = itemView.findViewById(R.id.profile_layout_item_extra)
    }

    override fun getItemCount(): Int {
        return profileArrayList.size
    }

    fun setOnClickItem(callback: (FriendData) -> Unit) {
        this.setOnClickItem = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(tempStaffList: ArrayList<FriendData>) {
        this.profileArrayList = tempStaffList
        notifyDataSetChanged()
    }
}