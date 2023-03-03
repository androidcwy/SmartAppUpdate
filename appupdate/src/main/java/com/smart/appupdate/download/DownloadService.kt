package com.smart.appupdate.download

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.smart.appupdate.Constants
import com.smart.appupdate.R
import com.smart.appupdate.bean.AppInfo
import com.smart.appupdate.utils.ApkUtil
import com.smart.appupdate.utils.FileUtil
import com.smart.appupdate.utils.LogUtil
import com.smart.appupdate.utils.NotificationUtil
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

/**
 * @author JoeYe
 * @date 2023/1/18 16:35
 */
class DownloadService : Service() {

    companion object {
        fun startService(context: Context, appInfo: AppInfo) {

            val intent = Intent(context, DownloadService::class.java)
                .putExtra(Constants.KEY_APP_INFO, appInfo)
            context.startService(intent)
        }

        const val TAG: String = "DownloadService"
    }

    private lateinit var appInfo: AppInfo
    private val downloadManager: HttpDownloadManager = HttpDownloadManager.sInstance
    private var lastProgress: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }
        init(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun init(intent: Intent) {
        appInfo = intent.getParcelableExtra<AppInfo>(Constants.KEY_APP_INFO)!!

        appInfo.downloadPath?.let { FileUtil.createDirDirectory(it) }

        if (checkApk(File(appInfo.downloadPath, appInfo.apkName))) {
            //install apk
            done(File(appInfo.downloadPath, appInfo.apkName))
        } else {
            // 清空本地文件夹再下载 避免因为版本文件名不同堆积在本地
            appInfo.downloadPath?.let { FileUtil.clearDir(it) }
            download()
        }
    }



    @Synchronized
    private fun download() {
        GlobalScope.launch(Dispatchers.Main + CoroutineName(Constants.COROUTINE_NAME)) {
            downloadManager.downloadApk(appInfo.downloadPath!!, appInfo.apkUrl!!, appInfo.apkName!!)
                .collect {
                    when (it) {
                        is DownloadStatus.Start -> start()
                        is DownloadStatus.Downloading -> downloading(it.max, it.progress)
                        is DownloadStatus.Done -> done(it.apk)
                        is DownloadStatus.Cancel -> this@DownloadService.cancel()
                        is DownloadStatus.Error -> error(it.e)
                    }
                }
        }
    }

    private fun cancel() {
        LogUtil.d(TAG, "Download cancel")

        if (appInfo.smallIcon != 0) {
            NotificationUtil.cancelNotification(this@DownloadService, appInfo)
        } else {
            LogUtil.d(TAG, "Notification smallIcon not config!")
        }

        ListenerManagement.get().downloadStatusListeners.forEach { 
            it.onCancel()
        }
    }

    private fun error(e: Throwable) {
        LogUtil.e(TAG, "download error: $e")
        if (appInfo.smallIcon != 0) {
            NotificationUtil.showErrorNotification(
                this@DownloadService, appInfo.smallIcon!!,
                resources.getString(R.string.app_update_download_error),
                resources.getString(R.string.app_update_continue_downloading),
                appInfo
            )
        }

        ListenerManagement.get().downloadStatusListeners.forEach { it.onError(e) }
    }

    private fun downloading(max: Int, progress: Int) {
        LogUtil.d(TAG, "Download downloading max: $max progress: $progress")

        if (appInfo.smallIcon != 0) {
            val curr = (progress / max.toDouble() * 100.0).toInt()
            if (curr == lastProgress) return
            LogUtil.i(TAG, "downloading max: $max --- progress: $progress")
            lastProgress = curr
            val content = if (curr < 0) "" else "$curr%"
            NotificationUtil.showProgressNotification(
                this@DownloadService, appInfo.smallIcon!!,
                resources.getString(R.string.app_update_start_downloading),
                content, if (max == -1) -1 else 100, curr, appInfo
            )
        } else {
            LogUtil.d(TAG, "Notification smallIcon not config!")
        }

        ListenerManagement.get().downloadStatusListeners.forEach { it.onDownloading(max, progress) }
    }

    private fun start() {
        // 开始时显示通知栏
        LogUtil.d(TAG, "Download start")

        if (appInfo.smallIcon != 0) {
            NotificationUtil.showNotification(
                this@DownloadService, appInfo.smallIcon!!,
                resources.getString(R.string.app_update_start_download),
                resources.getString(R.string.app_update_start_download_hint),
                appInfo
            )
        } else {
            LogUtil.d(TAG, "Notification smallIcon not config!")
        }

        ListenerManagement.get().downloadStatusListeners.forEach { it.onStart() }
    }

    private fun done(apk: File) {
        LogUtil.d(TAG, "Download done")

        if (appInfo.smallIcon != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NotificationUtil.showDoneNotification(
                    this@DownloadService, appInfo.smallIcon!!,
                    resources.getString(R.string.app_update_download_completed),
                    resources.getString(R.string.app_update_click_hint),
                    Constants.AUTHORITIES!!, apk,
                    appInfo
                )
            }
        } else {
            LogUtil.d(TAG, "Notification smallIcon not config!")
        }

        Log.e("test0209", "on done call.....")
        if (appInfo.autoInstall!!) {
            Log.e("test0209", "on done autoInstall call.....")
            ApkUtil.installApk(this@DownloadService, Constants.AUTHORITIES!!, apk)
        }

        // release objects
        releaseResources()

        ListenerManagement.get().downloadStatusListeners.forEach { it.onDone(apk) }
    }

    private fun checkApk(file: File): Boolean {
        if (file.exists()) {
            return if (!TextUtils.isEmpty(appInfo.apkMD5)) FileUtil.md5(file) == appInfo.apkMD5 else file.length() == appInfo.apkSize
        }
        return false
    }

    private fun releaseResources() {
        stopSelf()
    }
}