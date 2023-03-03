package com.smart.appupdate.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.smart.appupdate.Constants
import com.smart.appupdate.R
import com.smart.appupdate.bean.AppInfo
import com.smart.appupdate.download.DownloadService
import com.smart.appupdate.download.DownloadStatusListenerAdapter
import com.smart.appupdate.download.ListenerManagement
import com.smart.appupdate.utils.ConvertUtil
import com.smart.appupdate.utils.LogUtil
import com.smart.appupdate.utils.getSpValue
import com.smart.appupdate.utils.putSpValue
import kotlinx.android.synthetic.main.layout_app_dialog_update.*
import java.io.File


/**
 * 更新框口样式配置和实现
 * @author JoeYe
 * @date 2023/1/18 13:56
 */
class AppUpdateDialog : DialogFragment() {

    companion object{
        const val TAG = "UpgradeDialog"

        fun newInstance(appInfo: AppInfo): AppUpdateDialog {
            val args = Bundle()
            args.putParcelable(Constants.KEY_APP_INFO, appInfo)
            val fragment = AppUpdateDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val permissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE)
    private var isShowing: Boolean = false
    private lateinit var appInfo: AppInfo
    private var permissionsLauncher: ActivityResultLauncher<Array<String>>? = null

    private val downloadListenerAdapter = object : DownloadStatusListenerAdapter() {
        override fun onStart() {
            btnUpdateLater.isClickable = false
            btnUpdateNow.isClickable = false
            tvErrMsg.visibility = View.GONE
            nbpProgress.visibility = View.VISIBLE
            nbpProgress.progress = 0
            nbpProgress.max = 100
        }

        override fun onDownloading(max: Int, progress: Int) {
            nbpProgress.progress = ConvertUtil.getProgressPercentNumber(max, progress)
        }

        override fun onDone(apk: File) {
            btnUpdateLater.isClickable = true
            btnUpdateNow.isClickable = true

            if (apk.exists()) {
                // 刷新UI
                btnUpdateNow.text = getString(R.string.app_update_dialog_current_install_text)
                nbpProgress.visibility = View.GONE
            }
        }

        override fun onError(e: Throwable) {
            btnUpdateLater.isClickable = true
            btnUpdateNow.isClickable = true
            tvErrMsg.visibility = View.VISIBLE
            nbpProgress.visibility = View.GONE
            btnUpdateNow.text = getString(R.string.app_update_dialog_update_retry)
        }

        override fun onCancel() {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏
        setStyle(STYLE_NO_TITLE, R.style.BaseDialogFragment)
    }

    override fun onStart() {
        super.onStart()
        configDialog()
    }

    private fun configDialog() {
        val dialog = dialog
        if (dialog != null) {
            val dialogWindow = dialog.window
            if (dialogWindow != null) {
                dialogWindow.setGravity(Gravity.CENTER)
                dialogWindow.setLayout(
                    getWidth(dialogWindow) * 8 / 10,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialogWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
            }
        }
    }

    private fun getWidth(dialogWindow: Window): Int {
        val wm = dialogWindow.windowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_app_dialog_update, container, false)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            if (isShowing) {
                return
            }
            super.show(manager, tag)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            // 不要使用super.dismiss()，会出现Can not perform this action after onSaveInstanceState异常
            // 当Activity被杀死或者按下Home回调用系统的onSaveInstance(),保存状态后，如果再次执行dismiss()会报错
            super.dismissAllowingStateLoss()
            isShowing = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appInfo = requireArguments().getParcelable(Constants.KEY_APP_INFO)!!

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            run out@{
                it.entries.forEach { entry ->
                    if (!entry.value) {
                        // 无权限时 中断流程
                        LogUtil.e(TAG, "should config $entry permission first!!!")

                        // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                        Toast.makeText(
                            activity,
                            resources.getString(R.string.app_update_dialog_update_permission),
                            Toast.LENGTH_LONG
                        ).show()
                        return@out
                    }
                }

                // 走到这里代表权限已经全部通过 可以开启调用更新流程
                startUpdate()
            }
        }

        appInfo.let { it ->

            ListenerManagement.get().downloadStatusListeners.add(downloadListenerAdapter)

            tvVersion.visibility = if (TextUtils.isEmpty(it.newVersionText)) View.GONE else View.VISIBLE
            tvVersion.text = if (TextUtils.isEmpty(it.newVersionText)) "" else getString(R.string.app_update_dialog_update_version, it.newVersionText)

            tvForceUpdate.visibility = if (it.forceUpgrade!!) View.VISIBLE else View.GONE

            tvContent.visibility = if (TextUtils.isEmpty(it.content)) View.GONE else View.VISIBLE
            tvContent.text = if (TextUtils.isEmpty(it.content)) getString(R.string.app_update_dialog_update_content) else it.content

            tvFileSize.visibility = if (it.apkSize == 0L) View.GONE else View.VISIBLE
            tvFileSize.text = if (it.apkSize== 0L) "" else getString(R.string.app_update_dialog_update_size, ConvertUtil.getSize(it.apkSize))

            btnUpdateLater.text = if (it.forceUpgrade!!) getString(R.string.app_update_dialog_app_exit) else getString(R.string.app_update_dialog_update_later_text)
            btnUpdateLater.setOnClickListener {cIt ->
                if (it.forceUpgrade!!) {
                    // kill app
                    Process.killProcess(Process.myPid())
                } else {
                    // 非用户主动行为时 才记录稍后更新的间隔时间
                    if (!appInfo.isUserAction) {
                        val nextTime = getSpValue(Constants.KEY_SP_TAG, requireContext(), Constants.KEY_SP_LATER_CLICK_NEXT_SHOW_TIME, 0L)
                        if (nextTime == 0L || System.currentTimeMillis() > nextTime) {
                            putSpValue(Constants.KEY_SP_TAG, requireContext(), Constants.KEY_SP_LATER_CLICK_NEXT_SHOW_TIME, System.currentTimeMillis() + Constants.LATER_CLICK_NEXT_SHOW_TIME_INTERVAL)
                        }
                    }

                    dismiss()
                }
            }

            File(it.downloadPath, it.apkName).let {apk ->
                btnUpdateNow.text = if (apk.exists() && apk.length() == it.apkSize) getString(R.string.app_update_dialog_current_install_text) else getString(R.string.app_update_dialog_current_update_text)
            }
            btnUpdateNow.setOnClickListener {nIt ->
                // 优先判断本地已经下载完
                val apk = File(it.downloadPath, it.apkName)
                if (apk.exists() && apk.length() == it.apkSize) {
                    startUpdate()
                    return@setOnClickListener
                }

                // 校验权限
                val deniedIndex = permissions.indexOfFirst { pIt ->
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        pIt
                    ) == PackageManager.PERMISSION_DENIED
                }
                if (deniedIndex == -1) {
                    // 权限已经全部申请成功 进入更新流程
                    startUpdate()
                } else {
                    permissionsLauncher!!.launch(permissions.copyOfRange(deniedIndex, permissions.size))
                }
            }
        }
    }

    private fun startUpdate() {
        DownloadService.startService(requireContext(), appInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        ListenerManagement.get().downloadStatusListeners.remove(downloadListenerAdapter)
    }
}