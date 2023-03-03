package com.smart.appupdate.example.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * @author JoeYe
 * @date 2023/2/7 16:59
 */
object GsonUtils {

    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    fun toJson(o: Any?): String? {
        return gson.toJson(o)
    }

    fun <T> toObject(json: String?, classOfT: Class<T>?): T? {
        return if (StringUtil.isNull(json)) {
            null
        } else gson.fromJson(json, classOfT)
    }

}