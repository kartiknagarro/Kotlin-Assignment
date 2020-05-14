package com.nagarro.todos.domain

import com.nagarro.todos.BuildConfig
import com.nagarro.todos.data.dataModal.Todo
import com.nagarro.todos.data.network.api.ApiService

class TodoUseCaseImp(private val apiService: ApiService) : TodoUseCase {
    private lateinit var dataList: List<Todo>
    /*
    * At first, will fetch data from server and store it in local variable @dataList,
    * then return list of data of size as per BuildConfig.DEFAULT_PAGE_SIZE. On further call to this method
    * with increasing page count, will return list of same size as previous or return remaining data in case of less data than
    * BuildConfig.DEFAULT_PAGE_SIZE. if all data is exhausted then return empty list.
    *
    * */
    override suspend fun fetchTodoListAsync(page: Int): List<Todo> {
        if (page > 0) {
            val startIndex = BuildConfig.DEFAULT_PAGE_SIZE * page

            return if (startIndex < dataList.size) {
                val remainingData = dataList.size - startIndex
                val dataSize = if (remainingData > BuildConfig.DEFAULT_PAGE_SIZE) {
                    BuildConfig.DEFAULT_PAGE_SIZE
                } else {
                    remainingData
                }
                val lastIndex = BuildConfig.DEFAULT_PAGE_SIZE * page + dataSize

                dataList.subList(startIndex, lastIndex)
            } else {
                emptyList()
            }
        } else {
            return try {
                dataList = apiService.getTodos()
                if (dataList.size > BuildConfig.DEFAULT_PAGE_SIZE) {
                    dataList.subList(0, BuildConfig.DEFAULT_PAGE_SIZE)
                } else {
                    dataList
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}