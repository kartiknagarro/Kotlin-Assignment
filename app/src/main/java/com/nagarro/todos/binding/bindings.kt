package com.nagarro.todos.binding

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("position")
fun setPosition(view: RecyclerView?, pos: Int?) {
    (view?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos ?: 0, 0)
}

@BindingAdapter("refreshListener")
fun SwipeRefreshLayout.setRefreshListener(listener: SwipeRefreshLayout.OnRefreshListener) {
    this.setOnRefreshListener(listener)
}

@BindingAdapter("isRefresh")
fun SwipeRefreshLayout.isRefresh(isRefresh: Boolean) {
    this.isRefreshing = isRefresh
}

@SuppressLint("DefaultLocale")
@BindingAdapter("sentenceCase")
fun TextView.sentenceCase(text:String) {
    this.text = text.capitalize()
}