package com.smart.appupdate.utils

import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

object FileUtil {
    fun createDirDirectory(path: String) {
        File(path).let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }

    fun clearDir(path: String) {
        File(path).let {
            if (it.exists()) {
                it.deleteRecursively()
            }
        }
    }

    fun md5(file: File): String {
        try {
            val buffer = ByteArray(1024)
            var len: Int
            val digest = MessageDigest.getInstance("MD5")
            val inStream = FileInputStream(file)
            while (inStream.read(buffer).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            inStream.close()
            val bigInt = BigInteger(1, digest.digest())
            return bigInt.toString(16).uppercase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getFileNameFromUrl(url: String): String{
        return url.substring(url.lastIndexOf(File.separator) + 1)
    }
}