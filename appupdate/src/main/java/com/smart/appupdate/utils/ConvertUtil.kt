package com.smart.appupdate.utils

import java.text.NumberFormat

/**
 * @author JoeYe
 * @date 2023/2/7 09:54
 */
object ConvertUtil {

    fun getSize(size: Long): String {
        var s = ""
        val kb = (size / 1024).toDouble()
        val mb = kb / 1024
        val gb = mb / 1024
        val tb = gb / 1024
        if (size < 1024L) {
            s = "$size Bytes"
        } else if (size >= 1024 && size < 1024L * 1024) {
            s = String.format("%.2f", kb) + " KB"
        } else if (size >= 1024L * 1024 && size < 1024L * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB"
        } else if (size >= 1024L * 1024 * 1024 && size < 1024L * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB"
        } else if (size >= 1024L * 1024 * 1024 * 1024) {
            s = String.format("%.2f", tb) + " TB"
        }
        return s
    }

    fun getProgressPercentNumber(max: Int, progress: Int): Int{
        val numberFormat: NumberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(progress.toFloat() / max.toFloat() * 100).toInt()
    }
}