package com.smart.appupdate.utils

import android.app.Application
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.lang.reflect.InvocationTargetException

/**
 * @author JoeYe
 * @date 2023/2/6 11:40
 */
object AppGlobals {
    private var sApplication: Application? = null

    fun getApplication(): Application {
        if (sApplication == null) {
            try {
                sApplication = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null) as Application
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return sApplication!!
    }

    fun getString(@StringRes resId: Int): String? {
        return getApplication()!!.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg args: Any?): String? {
        return getApplication()!!.getString(resId, *args)
    }

    fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(getApplication()!!, color)
    }
}