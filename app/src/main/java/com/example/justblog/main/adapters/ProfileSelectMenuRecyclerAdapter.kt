package com.example.justblog.main.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.justblog.R
import com.example.justblog.main.model.PostData
import com.example.justblog.main.model.ProfileSelectData
import com.google.firebase.firestore.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileSelectMenuRecyclerAdapter(
    val context: Context,
    private var postDataArrayList: ArrayList<ProfileSelectData>,
) : RecyclerView.Adapter<ProfileSelectMenuRecyclerAdapter.ViewHolder>() {
    val linearLayoutList=ArrayList<LinearLayout>()
    private lateinit var mListener: OnItemClickListener

    fun setOnClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_profile_select_recyclerview_item, parent, false)
        return ViewHolder(v,mListener)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postDataArrayList[position]
        holder.bindView(item)

        if (position==0){
            holder.linearLayout.background= ContextCompat.getDrawable(
                context,
                R.drawable.ic_arena_recyclerview_linear_layout_textview_background
            )
        }
        linearLayoutList.add(holder.linearLayout)
    }

    inner class ViewHolder(itemView: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val linearLayout:LinearLayout=itemView.findViewById(R.id.select_recyclerview_item_linearlayout)
        private val menuName:TextView=itemView.findViewById(R.id.select_recyclerview_item_text)
        @SuppressLint("SetTextI18n")
        fun bindView(item: ProfileSelectData) {
            menuName.text=item.name
        }

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition,linearLayout,menuName,linearLayoutList)
            }
        }
    }

    override fun getItemCount(): Int {
        return postDataArrayList.size
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, linearlayout:LinearLayout, textview:TextView,linearlayoutList: ArrayList<LinearLayout>)
    }
}