package com.example.justblog


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RecyclerViewPicAdapter(
    val context: Context,
    private var accountDataArrayList: ArrayList<String>,
) : RecyclerView.Adapter<RecyclerViewPicAdapter.ViewHolder>(){
   private var setOnClickItem: ((String) -> Unit)? = null



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.login_recycler, parent, false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       holder.imageViewUserImage.setOnClickListener {
           setOnClickItem?.invoke(accountDataArrayList[position])
       }
       Glide.with(context).load(accountDataArrayList[position]).into(holder.imageViewUserImage)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewUserImage : ImageView = itemView.findViewById(R.id.imageview_login_activity_user_image)
    }

    override fun getItemCount(): Int {
        return accountDataArrayList.size
    }

    fun setOnClickItem(callback: (String) -> Unit) {
        this.setOnClickItem = callback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(tempStaffList: ArrayList<String>) {
        this.accountDataArrayList = tempStaffList
        notifyDataSetChanged()
    }
}