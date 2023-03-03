package com.smart.appupdate.download

import java.io.File

/**
 * @author JoeYe
 * @date 2023/1/18 14:25
 */
interface DownloadStatusListener{

    fun onStart()

    fun onDownloading(max: Int, progress: Int)

    fun onDone(apk: File)

    fun onCancel()

    fun onError(throwable: Throwable)
}