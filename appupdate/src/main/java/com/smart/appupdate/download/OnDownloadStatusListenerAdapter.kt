package com.smart.appupdate.download

import java.io.File

abstract class OnDownloadStatusListenerAdapter : DownloadStatusListener {
    override fun onStart() {
    }

    override fun onDownloading(max: Int, progress: Int) {
    }

    override fun onDone(apk: File) {
    }

    override fun onCancel() {
    }

    override fun onError(e: Throwable) {
    }
}