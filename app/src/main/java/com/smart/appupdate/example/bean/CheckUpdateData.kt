package com.smart.appupdate.example.bean

/**
 * @author JoeYe
 * @date 2023/2/7 16:56
 */
data class CheckUpdateData constructor(
    var newAppUpdateDesc: String = "",
    var newAppUrl: String = "",
    var newAppVersionName: String = "",
    var newAppSize: Long = 0,
    var newAppVersionCode: Int = 0,
    var appName: String = "",
    var id: Int = 0,
    var isForceUpdate: String = "",
    var createdBy: String = "",
    var lastModifiedBy: String = "",
    var lastModifiedDate: String = "",
    var minOldVersion: Int = 0,
    var newAppReleaseTime: String = "",
    var createdDate: String = "",
    var md5: String = "",
    var hotfix: Boolean = false,
)
