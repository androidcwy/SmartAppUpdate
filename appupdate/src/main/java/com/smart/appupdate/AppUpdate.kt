package com.smart.appupdate

import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.smart.appupdate.bean.AppInfo
import com.smart.appupdate.dialog.AppUpdateDialog
import com.smart.appupdate.utils.AppGlobals
import com.smart.appupdate.utils.FileUtil
import com.smart.appupdate.utils.LogUtil
import com.smart.appupdate.utils.getSpValue
import java.io.File

/**
 * @author JoeYe
 * @date 2023/1/13 10:30
 */
class AppUpdate (private var appInfo: AppInfo) {
    companion object{
        const val TAG = "AppUpdate"
    }

    fun show(activity: FragmentActivity) {
        if (!checkParam()) {
          return
        }
        // 非用户主动行为 需要校验是否存在稍后更新的时间 默认30分钟
        if (!appInfo.isUserAction) {
            val nextTime = getSpValue(Constants.KEY_SP_TAG, activity, Constants.KEY_SP_LATER_CLICK_NEXT_SHOW_TIME, 0L)
            if (nextTime != 0L && nextTime > System.currentTimeMillis()) {
                // 属于稍后更新的情况 暂时不需要显示
                LogUtil.d(TAG, "Won't show dialog because currently still within interval time")
                return
            }
        }
        AppUpdateDialog.newInstance(appInfo)
            .showNow(activity.supportFragmentManager, Constants.FRAGMENT_TAG)
    }

    private fun checkParam() : Boolean {
        if (TextUtils.isEmpty(appInfo.apkUrl)) {
            Toast.makeText(AppGlobals.getApplication(), "应用下载链接不能为空", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(appInfo.apkName)) {
            appInfo.apkName = FileUtil.getFileNameFromUrl(appInfo.apkUrl!!)
        }

        if(TextUtils.isEmpty(appInfo.downloadPath)){
            appInfo.downloadPath = AppGlobals.getApplication().cacheDir.absolutePath + File.separator + Constants.FAULT_DOWNLOAD_PATH;
        }
        Constants.AUTHORITIES = "${AppGlobals.getApplication().packageName}.fileProvider"
        return true
    }


}