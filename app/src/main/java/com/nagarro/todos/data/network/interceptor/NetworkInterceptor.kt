package com.nagarro.todos.data.network.interceptor

import android.content.Context
import com.nagarro.todos.R
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class NetworkInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder().build()
        val response = chain.proceed(newRequest)

        return when (response.code) {
            200 -> response
            500 -> throw IOException(context.getString(R.string.technical_error))
            408 -> throw IOException(context.getString(R.string.timeout_error))
            else -> throw IOException(context.getString(R.string.something_went_wrong))
        }
    }
}