package com.smart.appupdate.download

import java.io.File

/**
 * @author JoeYe
 * @date 2023/1/18 14:25
 */
open class DownloadStatusListenerAdapter: DownloadStatusListener {

    override fun onStart() {
    }

    override fun onDownloading(max: Int, progress: Int) {
    }

    override fun onDone(apk: File) {
    }

    override fun onCancel() {
    }

    override fun onError(throwable: Throwable) {
    }


}