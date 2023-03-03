package com.smart.appupdate.example.utils

import android.text.TextUtils

/**
 * @author JoeYe
 * @date 2023/2/7 16:59
 */
object StringUtil {

    fun join(vararg args: String?): String? {
        val sb = StringBuilder()
        if (args != null) {
            for (arg in args) {
                sb.append(arg)
            }
        }
        return sb.toString()
    }

    fun joinWithSeparator(separator: String?, vararg args: String?): String? {
        val sb = StringBuilder()
        if (args != null) {
            for (i in args.indices) {
                if (!TextUtils.isEmpty(args[i])) {
                    sb.append(args[i])
                    if (i != args.size - 1) {
                        sb.append(separator)
                    }
                }
            }
        }
        return sb.toString()
    }

    fun isNull(str: String?): Boolean {
        return TextUtils.isEmpty(str) || "null".equals(str, ignoreCase = true)
    }

    fun isListNull(str: String?): Boolean {
        return isNull(str) || "null".equals(str, ignoreCase = true)
    }

}