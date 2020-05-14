package com.nagarro.todos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.data.network.api.ApiService
import com.nagarro.todos.domain.TodoUseCaseImp
import io.mockk.MockKAnnotations
import io.mockk.MockKException
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var apiService: ApiService

    private val useCase by lazy {
        TodoUseCaseImp(apiService)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }


    @Test
    fun fetchTodoInitialWhenMinimumData() {
        runBlockingTest {
            val list = MutableList(5, init = {
                Todo(it, "Demo", false)
            })
            coEvery { apiService.getTodos() } returns list
            val data = useCase.fetchTodoListAsync(0)
            Assert.assertTrue(data.size == 5)
        }
    }


    @Test
    fun fetchTodoInitialWhenMoreThanMinimum() {
        runBlockingTest {
            val list = MutableList(20, init = {
                Todo(it, "Demo", false)
            })
            coEvery { apiService.getTodos() } returns list
            val data = useCase.fetchTodoListAsync(0)
            Assert.assertTrue(data.size == 10)
        }
    }

    @Test
    fun fetchTodoForPagination() {
        runBlockingTest {
            val list = MutableList(18, init = {
                Todo(it, "Demo", false)
            })
            coEvery { apiService.getTodos() } returns list
            val initialData = useCase.fetchTodoListAsync(0)
            val nextData = useCase.fetchTodoListAsync(1)
            Assert.assertTrue(initialData.size == 10)
            Assert.assertTrue(nextData.size == 8)
        }
    }

    @Test
    fun fetchTodoWhenAllDataConsumed() {
        runBlockingTest {
            val list = MutableList(10, init = {
                Todo(it, "Demo", false)
            })
            coEvery { apiService.getTodos() } returns list
            val initialData = useCase.fetchTodoListAsync(0)
            val nextData = useCase.fetchTodoListAsync(1)
            Assert.assertTrue(initialData.size == 10)
            Assert.assertTrue(nextData.isEmpty())
        }
    }

    @Test(expected = MockKException::class)
    fun fetchTodoWhenError() {
        runBlockingTest {
            coEvery { apiService.getTodos() } throws MockKException("")
            useCase.fetchTodoListAsync(0)
        }
    }
}