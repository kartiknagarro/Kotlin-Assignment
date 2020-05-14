package com.nagarro.todos

import android.app.Application
import com.nagarro.todos.di.appModule
import com.nagarro.todos.di.networkModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class MainApplication : Application(), KodeinAware {
    /*
    * Will register all modules in kodein DI
    * */
    override val kodein by Kodein.lazy {
        import(networkModule)
        import(appModule)
    }
}