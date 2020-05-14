package com.nagarro.todos.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import com.nagarro.todos.BuildConfig

object Utils {
    var BASE_URL = BuildConfig.BASE_URL

    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork != null
        } else {
            cm.activeNetworkInfo != null
        }
    }


}