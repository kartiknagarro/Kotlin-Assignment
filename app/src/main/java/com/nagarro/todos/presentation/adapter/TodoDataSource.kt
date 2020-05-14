package com.nagarro.todos.presentation.adapter

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.nagarro.todos.R
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.domain.TodoUseCase
import com.nagarro.todos.util.LoadingState
import kotlinx.coroutines.*

class TodoDataSource(
    private val context: Context,
    private val useCase: TodoUseCase,
    private val isLoading: MutableLiveData<LoadingState>,
    private val message: MutableLiveData<String>,
    private val dispatcher: CoroutineDispatcher
) : PageKeyedDataSource<Int, Todo>() {

    /**
     * Run all coroutine independently, if any one get cancelled, it will prevent other to get cancel.
     */
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    /*
    * exception handler for api call
    * */
    private val exceptionHandler = CoroutineExceptionHandler { _, Exception ->
        message.postValue(Exception.message)
        isLoading.postValue(LoadingState.ERROR)
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Todo>
    ) {
        /*
        * Do not update loading state if it is Refresh case
        * */
        if (isLoading.value != LoadingState.REFRESH)
            isLoading.postValue(LoadingState.INITIAL)

        scope.launch(dispatcher + exceptionHandler) {
            val result = useCase.fetchTodoListAsync(0)
            callback.onResult(result, 0, 1)
            isLoading.postValue(LoadingState.NONE)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Todo>) {
        isLoading.postValue(LoadingState.NEXT)

        scope.launch(dispatcher + exceptionHandler) {
            val result = useCase.fetchTodoListAsync(params.key)
            if (result.isNotEmpty()) {
                callback.onResult(result, params.key + 1)
            } else {
                message.postValue(context.getString(R.string.no_more_data))
            }

            isLoading.postValue(LoadingState.NONE)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Todo>) {}

    /*
    * cancel all jobs running in parent coroutine
    * */
    override fun invalidate() {
        super.invalidate()
        if (scope.coroutineContext.isActive)
            scope.coroutineContext.cancelChildren()
    }
}