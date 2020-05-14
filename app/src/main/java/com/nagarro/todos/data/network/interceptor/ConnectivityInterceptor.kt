package com.nagarro.todos.data.network.interceptor

import android.content.Context
import com.nagarro.todos.R
import com.nagarro.todos.util.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (Utils.isInternetAvailable(context))
            chain.proceed(chain.request())
        else {
            throw IOException(context.getString(R.string.no_internet))
        }
    }
}