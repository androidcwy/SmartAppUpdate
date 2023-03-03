package com.smart.appupdate.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.smart.appupdate.Constants
import com.smart.appupdate.bean.AppInfo
import com.smart.appupdate.download.DownloadService
import java.io.File

object NotificationUtil {

    fun notificationEnable(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getNotificationChannelId(appInfo: AppInfo): String {
        val channel = appInfo.notificationChannel
        return if (channel == null) {
            Constants.DEFAULT_CHANNEL_ID
        } else {
            channel.id
        }
    }

    private fun builderNotification(
        context: Context, icon: Int, title: String, content: String, appInfo: AppInfo
    ): NotificationCompat.Builder {
        var channelId = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = getNotificationChannelId(appInfo)
        }
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setWhen(System.currentTimeMillis())
            .setContentText(content)
            .setAutoCancel(false)
            .setOngoing(true)
    }

    fun showNotification(context: Context, icon: Int, title: String, content: String, appInfo: AppInfo) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            afterO(manager, appInfo)
        }
        val notify = builderNotification(context, icon, title, content, appInfo)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()
        manager.notify(appInfo.notifyId, notify)
    }

    /**
     * send a downloading Notification
     */
    fun showProgressNotification(
        context: Context, icon: Int, title: String, content: String, max: Int, progress: Int,
        appInfo: AppInfo
    ) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notify = builderNotification(context, icon, title, content, appInfo)
            .setProgress(max, progress, max == -1).build()
        manager.notify(appInfo.notifyId, notify)
    }

    /**
     * send a downloaded Notification
     */
    fun showDoneNotification(
        context: Context, icon: Int, title: String, content: String,
        authorities: String, apk: File, appInfo: AppInfo
    ) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(appInfo.notifyId)
        val intent = ApkUtil.createInstallIntent(context, authorities, apk)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notify = builderNotification(context, icon, title, content, appInfo)
            .setContentIntent(pi)
            .build()
        notify.flags = notify.flags or Notification.FLAG_AUTO_CANCEL
        manager.notify(appInfo.notifyId, notify)
    }

    /**
     * send a error Notification
     */
    fun showErrorNotification(
        context: Context, icon: Int, title: String, content: String, appInfo: AppInfo
    ) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            afterO(manager, appInfo)
        }
        val intent = Intent(context, DownloadService::class.java)
        val pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notify = builderNotification(context, icon, title, content, appInfo)
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(pi)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()
        manager.notify(appInfo.notifyId, notify)
    }

    /**
     * cancel Notification by id
     */
    fun cancelNotification(context: Context, appInfo: AppInfo) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(appInfo.notifyId)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun afterO(manager: NotificationManager, appInfo: AppInfo) {
        var channel = appInfo.notificationChannel
        if (channel == null) {
            channel = NotificationChannel(
                Constants.DEFAULT_CHANNEL_ID, Constants.DEFAULT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableLights(true)
                setShowBadge(true)
            }
        }
        manager.createNotificationChannel(channel)
    }
}