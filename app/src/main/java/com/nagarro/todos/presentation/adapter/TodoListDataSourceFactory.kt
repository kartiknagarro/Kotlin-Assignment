package com.nagarro.todos.presentation.adapter

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.domain.TodoUseCase
import com.nagarro.todos.util.LoadingState
import kotlinx.coroutines.CoroutineDispatcher

class TodoListDataSourceFactory(
    private val context: Context,
    private val todoUseCase: TodoUseCase,
    private val isLoading: MutableLiveData<LoadingState>,
    private val message: MutableLiveData<String>,
    private val dispatcher: CoroutineDispatcher
) : DataSource.Factory<Int, Todo>() {

    val liveDataSource = MutableLiveData<TodoDataSource>()
    private lateinit var dataSource: TodoDataSource

    override fun create(): DataSource<Int, Todo> {
        dataSource = TodoDataSource(context, todoUseCase, isLoading, message, dispatcher)
        liveDataSource.postValue(dataSource)
        return dataSource
    }
}