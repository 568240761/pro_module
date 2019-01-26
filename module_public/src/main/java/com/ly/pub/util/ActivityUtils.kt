package com.ly.pub.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.PUBLIC_LOGIN_ACTIVITY
import java.util.*

/**
 * Created by LanYang on 2018/8/6
 * 管理页面
 */
private val list = ArrayList<Activity>()

fun getActivitySize(): Int {
    return list.size
}

internal fun addActivity(activity: Activity) {
    list.add(activity)
}

internal fun removeActivity(activity: Activity) {
    list.remove(activity)
}

fun isContainActivity(activity: Activity): Boolean {
    return list.contains(activity)
}

fun getLastActivity(): Activity {
    return list.last()
}

/**
 * 清除所有activity
 */
fun clearAllActivity() {
    for (activity in list) {
        activity.finish()
    }
}

/**
 * 返回Context对应的Activity
 * @param context 上下文环境
 */
fun getCurrentActivity(context: Context): Activity? {
    when (context) {
        is FragmentActivity -> return context
        is Activity -> return context
        is ContextWrapper -> return getCurrentActivity(context.baseContext)
    }
    return null
}

/**
 * 状态异常时，需要重新登录
 * @param activity 当前页面,默认为null
 */
fun gotoLoginActivity(activity: Activity? = null) {
    if (activity == null) {
        val intent = Intent(PUBLIC_APPLICATION, PUBLIC_LOGIN_ACTIVITY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        PUBLIC_APPLICATION.startActivity(intent)
    } else {
        jumpNewPageFromLeft(
            activity,
            PUBLIC_LOGIN_ACTIVITY,
            flag = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        )
    }
}

