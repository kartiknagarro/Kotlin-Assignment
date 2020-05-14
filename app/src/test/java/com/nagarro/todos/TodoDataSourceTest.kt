package com.nagarro.todos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource.*
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.domain.TodoUseCase
import com.nagarro.todos.presentation.adapter.TodoDataSource
import com.nagarro.todos.util.LoadingState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodoDataSourceTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var useCase: TodoUseCase

    @MockK
    lateinit var app: MainApplication

    private val loadParams = LoadParams<Int>(1, 2)

    private val message = spyk<MutableLiveData<String>>()
    private val isLoading = spyk<MutableLiveData<LoadingState>>()
    private val loadInitialCallback = spyk<LoadInitialCallback<Int, Todo>>()
    private val loadCallback = spyk<LoadCallback<Int, Todo>>()
    private val dataSource by lazy {
        TodoDataSource(
            app,
            useCase,
            isLoading,
            message,
            Dispatchers.Unconfined
        )
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun loadInitialWhenDataAvailable() {
        val list = listOf(
            Todo(1, "", false),
            Todo(2, "", false),
            Todo(3, "", false)
        )
        coEvery { useCase.fetchTodoListAsync(0) } returns list

        dataSource.loadInitial(
            params = LoadInitialParams<Int>(0, false),
            callback = loadInitialCallback
        )
        verifyOrder {
            isLoading.postValue(LoadingState.INITIAL)
            loadInitialCallback.onResult(list, 0, 1)
            isLoading.postValue(LoadingState.NONE)
        }
        confirmVerified()
    }

    @Test
    fun loadInitialWhenRefresh() {
        val list = listOf(
            Todo(1, "", false),
            Todo(2, "", false),
            Todo(3, "", false)
        )
        coEvery { useCase.fetchTodoListAsync(0) } returns list
        isLoading.postValue(LoadingState.REFRESH)
        dataSource.loadInitial(
            params = LoadInitialParams<Int>(0, false),
            callback = loadInitialCallback
        )
        verifyOrder {
            loadInitialCallback.onResult(list, 0, 1)
            isLoading.postValue(LoadingState.NONE)
        }
        confirmVerified()
    }

    @Test
    fun loadInitialWhenException() {
        coEvery { useCase.fetchTodoListAsync(0) } throws MockKException("Error")

        dataSource.loadInitial(
            params = LoadInitialParams<Int>(0, false),
            callback = loadInitialCallback
        )
        verifyOrder {
            isLoading.postValue(LoadingState.INITIAL)
            message.postValue("Error")
            isLoading.postValue(LoadingState.ERROR)
        }
        confirmVerified()
    }

    @Test
    fun loadAfterWhenDataAvailable() {
        val list = listOf(
            Todo(1, "", false),
            Todo(2, "", false),
            Todo(3, "", false)
        )
        coEvery { useCase.fetchTodoListAsync(1) } returns list
        dataSource.loadAfter(
            params = LoadParams(1, 1),
            callback = loadCallback
        )
        verifyOrder {
            isLoading.postValue(LoadingState.NEXT)
            loadCallback.onResult(list, 2)
            isLoading.postValue(LoadingState.NONE)
        }
        confirmVerified()
    }

    @Test
    fun loadAfterWhenNoData() {
        every { app.getString(R.string.no_more_data) } returns "No more data available"
        coEvery { useCase.fetchTodoListAsync(1) } returns emptyList()
        dataSource.loadAfter(
            params = loadParams,
            callback = loadCallback
        )
        verifyOrder {
            isLoading.postValue(LoadingState.NEXT)
            message.postValue("No more data available")
            isLoading.postValue(LoadingState.NONE)
        }
        confirmVerified()
    }

}