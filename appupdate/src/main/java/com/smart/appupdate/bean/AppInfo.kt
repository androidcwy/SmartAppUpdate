package com.smart.appupdate.bean

import android.app.NotificationChannel
import android.os.Parcelable
import com.smart.appupdate.Constants
import kotlinx.android.parcel.Parcelize

/**
 * @author JoeYe
 * @date 2023/1/13 10:33
 */
@Parcelize
open class AppInfo(
    internal var content: String? = null, internal var apkName: String = "",
    internal var apkUrl: String? = null, internal var apkMD5: String? = null,
    internal var apkSize: Long = 0L, internal var autoInstall: Boolean? = false,
    internal var forceUpgrade: Boolean? = false, internal var downloadPath: String? = null,
    internal var notificationChannel: NotificationChannel? = null, internal var notifyId: Int = Constants.DEFAULT_NOTIFY_ID,
    internal var smallIcon: Int? = 0, internal var newVersionText: String? = null,
    internal var isUserAction: Boolean = false
//    open var downloadStatusListeners: @RawValue MutableList<DownloadStatusListenerAdapter> = mutableListOf<DownloadStatusListenerAdapter>()
) : Parcelable {

//     var content: String? = null
//
//     var apkName: String = ""
//
//     var apkUrl: String = ""
//
//     var apkMD5: String? = null
//
//     var apkSize: Long = 0L
//
//     var autoInstall: Boolean = false
//
//     var forceUpgrade: Boolean = false
//
//     var downloadPath: String? = null
//
//     var notificationChannel: NotificationChannel? = null
//
//     var notifyId: Int = Constants.DEFAULT_NOTIFY_ID
//
//     var smallIcon: Int = 0
//
//     var newVersionText: String? = null  // 版本号信息
//
//     var downloadStatusListeners: @RawValue MutableList<DownloadStatusListenerAdapter> = mutableListOf<>()

    fun content(content: String?): AppInfo {
        this.content = content
        return this
    }

    fun apkName(apkName: String): AppInfo {
        this.apkName = apkName
        return this
    }

    fun apkUrl(apkUrl: String): AppInfo {
        this.apkUrl = apkUrl
        return this
    }

    fun apkMD5(apkMD5: String?): AppInfo {
        this.apkMD5 = apkMD5
        return this
    }

    fun apkSize(apkSize: Long): AppInfo {
        this.apkSize = apkSize
        return this
    }

    fun autoInstall(autoInstall: Boolean): AppInfo {
        this.autoInstall = autoInstall
        return this
    }

    fun forceUpgrade(forceUpgrade: Boolean): AppInfo {
        this.forceUpgrade = forceUpgrade
        return this
    }

    fun isUserAction(isUserAction: Boolean): AppInfo {
        this.isUserAction = isUserAction
        return this
    }

    fun downloadPath(downloadPath: String): AppInfo {
        this.downloadPath = downloadPath
        return this
    }

    fun notificationChannel(notificationChannel: NotificationChannel): AppInfo {
        this.notificationChannel = notificationChannel
        return this
    }

    fun notifyId(notifyId: Int): AppInfo {
        this.notifyId = notifyId
        return this
    }

    fun smallIcon(smallIcon: Int): AppInfo {
        this.smallIcon = smallIcon
        return this
    }

    fun newVersionText(newVersionText: String?): AppInfo {
        this.newVersionText = newVersionText
        return this
    }

//    fun downloadStatusListeners(downloadStatusListeners: MutableList<DownloadStatusListenerAdapter>): AppInfo {
//        this.downloadStatusListeners = downloadStatusListeners
//        return this
//    }
}