package com.nagarro.todos.domain

import com.nagarro.todos.data.dataModal.Todo

interface TodoUseCase {
   suspend fun fetchTodoListAsync(page: Int): List<Todo>
}