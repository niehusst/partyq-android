package com.niehusst.partyq.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isGone")
fun View.isGone(gone: Boolean) {
    if (gone) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}
