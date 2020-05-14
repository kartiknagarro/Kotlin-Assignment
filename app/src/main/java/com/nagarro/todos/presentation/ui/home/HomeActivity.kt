package com.nagarro.todos.presentation.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nagarro.todos.HomeViewModelFactory
import com.nagarro.todos.MainApplication
import com.nagarro.todos.R
import com.nagarro.todos.databinding.ActivityHomeBinding
import com.nagarro.todos.presentation.adapter.TodoListAdapter
import com.nagarro.todos.presentation.viewModal.home.HomeViewModal

class HomeActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var viewModel: HomeViewModal

    private val listAdapter by lazy {
        TodoListAdapter(viewModel.isLoading)
    }

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel =
            ViewModelProvider(
                this,
                HomeViewModelFactory(application as MainApplication)
            ).get(HomeViewModal::class.java)

        setBinding()
        setObserver()
    }

    private fun setObserver() {
        viewModel.getTodoList().observe(this, Observer {
            listAdapter.submitList(it)
            Handler().postDelayed({
                binding.list.smoothScrollToPosition(0)
            }, 200)
        })

        viewModel.message.observe(this, Observer {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setBinding() {
        binding.lifecycleOwner = this
        binding.adapter = listAdapter
        binding.isLoading = viewModel.isLoading
        binding.refreshListener = this
        binding.list.setHasFixedSize(true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        /*
        * store top visible index of recycler view, this will use
        * to render recycler view with same position on configuration change.
        * */
        viewModel.listVisiblePosition.value =
            (binding.list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onRefresh() {
        viewModel.refresh()
    }
}

