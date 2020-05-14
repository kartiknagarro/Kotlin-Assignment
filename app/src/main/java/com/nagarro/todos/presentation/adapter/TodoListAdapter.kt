package com.nagarro.todos.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nagarro.todos.R
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.databinding.ListItemBinding
import com.nagarro.todos.databinding.LoaderViewBinding
import com.nagarro.todos.util.LoadingState

const val VIEW_PROGRESS = 1
const val VIEW_ITEM = 2

class TodoListAdapter(private val loading: MutableLiveData<LoadingState>) :
    PagedListAdapter<Todo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemCount(): Int {
        return currentList?.size?.plus(1) ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount - 1 == position) VIEW_PROGRESS else VIEW_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_ITEM) {
            val binding = DataBindingUtil.inflate<ListItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item,
                parent,
                false
            )
            ListItemViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<LoaderViewBinding>(
                LayoutInflater.from(parent.context),
                R.layout.loader_view,
                parent,
                false
            )
            LoaderViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListItemViewHolder) {
            getItem(position)?.let { holder.bind(it) }
        } else {
            (holder as LoaderViewHolder).bind(loading)
        }
    }

    class LoaderViewHolder(private val binding: LoaderViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loading: MutableLiveData<LoadingState>) {
            binding.isLoading = loading
        }
    }

    class ListItemViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var todo: Todo
        fun bind(item: Todo) {
            todo = item
            binding.item = item
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(
                d1: Todo,
                d2: Todo
            ) = (d1.id == d2.id)

            override fun areContentsTheSame(
                d1: Todo,
                d2: Todo
            ) = (d1 == d2)
        }
    }

}