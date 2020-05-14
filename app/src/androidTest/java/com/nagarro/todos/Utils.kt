package com.nagarro.todos

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nagarro.todos.data.dataModal.Todo

object Utils {
    fun getJsonString(context: Context): String {
        val stream = context.resources.assets.open("data.json")
        val size = stream.available()
        val bytes = ByteArray(size)
        stream.use { stream ->
            stream.read(bytes)
        }
        return String(bytes)
    }

    fun getList(context: Context): List<Todo> {
        val listType = object : TypeToken<List<Todo>>() {}.type
        return Gson().fromJson(getJsonString(context), listType)
    }
}