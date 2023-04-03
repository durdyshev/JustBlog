package com.example.justblog.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.justblog.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

fun CircleImageView.urlImage(url: String?, progressDrawable: CircularProgressDrawable) {

    val options = RequestOptions()
        .placeholder(progressDrawable)
        .fitCenter()
        .error(R.drawable.noun_profile_1051511)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

fun placeholderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}

@BindingAdapter("android:imageFromUrl")
fun bindingImage(view: CircleImageView, url: String?) {
    view.urlImage(url, placeholderProgressBar(view.context))
}

fun ImageView.urlImage(url: String?, progressDrawable: CircularProgressDrawable) {

    val options = RequestOptions()
        .placeholder(progressDrawable)
        .fitCenter()
        .error(R.drawable.noun_profile_1051511)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

@BindingAdapter("android:imageFromUrl")
fun bindingImage(view: ImageView, url: String?) {
    view.urlImage(url, placeholderProgressBar(view.context))
}

fun TextView.getTimeAgo(date: Date, view: TextView) {
    val dateString =
        DateFormat.format("dd/MM/yyyy hh:mm", Date(date.time)).toString()
    view.text = dateString
}

@SuppressLint("SetTextI18n")
fun TextView.likeCount(countSize: Int, view: TextView) {
    if (countSize == 0) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        view.text = "Likes:$countSize"
    }

}

@SuppressLint("SetTextI18n")
fun TextView.commentCount(countSize: Int, view: TextView, num: Int) {
    if (countSize == 0) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        view.text = "Comments:$countSize"
    }

}

@BindingAdapter("android:getTimeAgo")
fun bindingImage(view: TextView, url: Date) {
    view.getTimeAgo(url, view)
}

@BindingAdapter("android:getLikes")
fun bindingImage(view: TextView, countSize: Int) {
    view.likeCount(countSize, view)
}

@BindingAdapter("android:getComments")
fun bindingImage(view: TextView, countSize: Int, num: Int) {
    view.commentCount(countSize, view, 1)
}

fun ImageView.checkHeart(view: ImageView, boolean: Boolean) {

    if (boolean) {
       view.setImageResource(R.drawable.baseline_favorite_24)
    } else {
       view.setImageResource(R.drawable.baseline_favorite_border_24)
    }
}
@BindingAdapter("android:checkHeart")
fun bindingImage(view: ImageView, boolean: Boolean) {
    view.checkHeart( view,boolean)
}