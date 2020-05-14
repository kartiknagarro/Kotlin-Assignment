package com.nagarro.todos.data.network.api

import com.nagarro.todos.data.dataModal.Todo
import retrofit2.http.GET

interface ApiService {
    @GET("todos")
    suspend fun getTodos():List<Todo>
}