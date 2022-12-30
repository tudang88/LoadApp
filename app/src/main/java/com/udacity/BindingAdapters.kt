package com.udacity

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Binding adapter for changing
 * status image
 */
@BindingAdapter("statusImage")
fun bindingStatusImage(imageView: ImageView, status: String) {
    if (status == "Success") {
        imageView.setImageResource(R.drawable.dholler_ok)
    } else {
        imageView.setImageResource(R.drawable.failed)
    }
}

/**
 * Binding adapter for changing status text color
 */
@BindingAdapter("statusText")
fun bindingStatusTextView(textView: TextView, status: String) {
    val context = textView.context
    if (status == "Success") {
        textView.setTextColor(context.getColor(R.color.colorPrimary))
    } else {
        textView.setTextColor(context.getColor(R.color.colorAccent))
    }
}