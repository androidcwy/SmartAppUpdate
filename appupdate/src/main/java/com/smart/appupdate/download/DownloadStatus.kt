package com.smart.appupdate.download

import java.io.File

/**
 * @author JoeYe
 * @date 2023/1/18 15:07
 */
sealed class DownloadStatus {

    object Start : DownloadStatus()

    data class Downloading(val max: Int, val progress: Int) : DownloadStatus()

    class Done(val apk: File) : DownloadStatus()

    object Cancel : DownloadStatus()

    data class Error(val e: Throwable) : DownloadStatus()
}