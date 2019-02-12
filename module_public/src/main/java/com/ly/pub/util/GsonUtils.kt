package com.ly.pub.util

import com.google.gson.Gson
import java.lang.reflect.Type

fun <T> gsonToEntity(str: String, type: Type): T {
    val gson = Gson()
    return gson.fromJson<T>(str, type)
}

fun <T> gsonToList(str: String, type: Type): List<T> {
    val gson = Gson()
    return gson.fromJson<List<T>>(str, type)
}

fun gsonMapToString(any: Any): String {
    val gson = Gson()
    return gson.toJson(any)
}