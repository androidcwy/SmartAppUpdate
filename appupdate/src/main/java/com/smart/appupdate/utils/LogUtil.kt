package com.smart.appupdate.utils

import android.util.Log

/**
 * ProjectName: AppUpdate
 * PackageName: com.azhon.appupdate.util
 * FileName:    LogUtil
 * CreateDate:  2022/4/7 on 11:23
 * Desc:
 *
 * @author azhon
 */

class LogUtil {

    companion object {
        const val TAG = "SmartAppUpdate"
        
        var b = true

        fun enable(enable: Boolean) {
            b = enable
        }

        fun e(tag: String, msg: String) {
            if (b) Log.e(TAG + tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (b) Log.d(TAG + tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (b) Log.i(TAG + tag, msg)
        }

    }
}