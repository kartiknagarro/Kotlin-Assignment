package com.nagarro.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nagarro.todos.presentation.viewModal.home.HomeViewModal

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(val app: MainApplication) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModal(app) as T
    }
}