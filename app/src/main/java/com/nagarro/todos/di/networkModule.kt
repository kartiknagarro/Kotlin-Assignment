package com.nagarro.todos.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nagarro.todos.BuildConfig
import com.nagarro.todos.MainApplication
import com.nagarro.todos.data.network.api.ApiService
import com.nagarro.todos.data.network.interceptor.ConnectivityInterceptor
import com.nagarro.todos.data.network.interceptor.NetworkInterceptor
import com.nagarro.todos.util.Utils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = Kodein.Module("NetworkModule", false, "Network") {
    bind() from contexted<MainApplication>().provider { context }
    bind() from singleton { getOkHttpClient(instance<MainApplication>().applicationContext) }
    bind() from singleton { getRetrofitService(instance()) }
    bind() from singleton { getApiService(instance()) }
}

fun getRetrofitService(client: OkHttpClient): Retrofit {
    return Retrofit.Builder().addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Utils.BASE_URL)
        .client(client)
        .build()
}

fun getApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}

fun getOkHttpClient(context: Context): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        clientBuilder.addInterceptor(logging)
    }

    return clientBuilder.addInterceptor(NetworkInterceptor(context))
        .addInterceptor(ConnectivityInterceptor(context)).build()
}