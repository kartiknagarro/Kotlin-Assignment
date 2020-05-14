package com.nagarro.todos.di

import com.nagarro.todos.domain.TodoUseCase
import com.nagarro.todos.domain.TodoUseCaseImp
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

val appModule = Kodein.Module("App-Module", false, "APP") {
    bind<TodoUseCase>() with provider { TodoUseCaseImp(instance()) }
}