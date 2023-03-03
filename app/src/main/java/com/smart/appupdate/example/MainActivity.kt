package com.smart.appupdate.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smart.appupdate.AppUpdate
import com.smart.appupdate.bean.AppInfo
import com.smart.appupdate.example.bean.CheckUpdateData
import com.smart.appupdate.example.config.DataConfig
import com.smart.appupdate.example.utils.GsonUtils
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author JoeYe
 * @date 2023/2/7 16:48
 */
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = GsonUtils.toObject(DataConfig.DATA, CheckUpdateData::class.java)!!

        // TODO for test
        data.newAppSize = 40504916;
        data.minOldVersion = 21;
        data.md5 = "89472E2160021980487C38E8A4415E07";
        data.newAppUpdateDesc = "1.用户体验优化\n2.部分问题修复";
        // TODO for test

        btnShowUpdateDialog.setOnClickListener {
            // 需要更新

            val appInfo: AppInfo = AppInfo()
                .apkUrl(data.newAppUrl)
                .apkMD5(data.md5)
                .autoInstall(true)
                .apkSize(data.newAppSize)
                .content(data.newAppUpdateDesc)
                .newVersionText(data.newAppVersionName)
                .isUserAction(false)
                .forceUpgrade(true)
            val appUpdate = AppUpdate(appInfo)
            appUpdate.show(this@MainActivity)
        }
    }

}