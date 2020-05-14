package com.nagarro.todos.presentation.viewModal.home

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nagarro.todos.BuildConfig
import com.nagarro.todos.MainApplication
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.domain.TodoUseCase
import com.nagarro.todos.presentation.adapter.TodoListDataSourceFactory
import com.nagarro.todos.util.LoadingState
import kotlinx.coroutines.Dispatchers
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import java.util.concurrent.Executors

class HomeViewModal(app: MainApplication) : AndroidViewModel(app), KodeinAware {

    override val kodein by app.kodein
    override val kodeinContext = kcontext(app)

    private val todoUseCase by instance<TodoUseCase>()

    private val dataSourceFactory by lazy {
        TodoListDataSourceFactory(
            app.applicationContext,
            todoUseCase,
            isLoading,
            message,
            Dispatchers.IO
        )
    }
    val listVisiblePosition by lazy {
        MutableLiveData<Int>()
    }

    val isLoading by lazy {
        MutableLiveData<LoadingState>()
    }

    val message by lazy {
        MutableLiveData<String>()
    }

    fun getTodoList(): LiveData<PagedList<Todo>> {
        val executor = Executors.newFixedThreadPool(5)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(BuildConfig.INITIAL_LOAD_SIZE)
            .setPrefetchDistance(BuildConfig.PREFETCH_DISTANCE)
            .setPageSize(BuildConfig.DEFAULT_PAGE_SIZE)
            .build()

        return LivePagedListBuilder(dataSourceFactory, config)
            .setFetchExecutor(executor)
            .build()
    }

    fun refresh() {
        isLoading.value = LoadingState.REFRESH
        dataSourceFactory.liveDataSource.value?.invalidate()
    }
}

