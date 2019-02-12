package com.ly.pub.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.PUBLIC_IS_LOG
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by LanYang on 2018/9/10
 * 辅助工具类
 */

/**
 * 使用MD5算法对传入的url生成唯一的key
 * @param url 接口
 */
fun hashUrl(url: String): String {
    return try {
        val mDigest = MessageDigest.getInstance("MD5")
        mDigest.update(url.toByteArray())
        bytesToHexString(mDigest.digest())
    } catch (e: NoSuchAlgorithmException) {
        url.hashCode().toString()
    }
}

private fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices) {
        val hex = Integer.toHexString(0xFF and bytes[i].toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}

/**
 * 当前进程名是否为应用包名
 *
 * @return 返回true, 表示相同;返回false,表示不相同
 */
fun isMainProcess(): Boolean {
    val pId = android.os.Process.myPid()
    LogUtil_d("isMainProcess", "当前进程ID为:$pId")
    var processName: String? = null
    val am = PUBLIC_APPLICATION.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val list = am.runningAppProcesses
    for (aList in list) {
        val info = aList as ActivityManager.RunningAppProcessInfo
        try {
            if (info.pid == pId) {
                processName = info.processName
                LogUtil_d("isMainProcess", "当前进程名为:" + processName!!)
            }
        } catch (e: Exception) {
            if (PUBLIC_IS_LOG) e.printStackTrace()
        }

    }

    return processName != null && processName.equals(PUBLIC_APPLICATION.getPackageName(), ignoreCase = true)
}

/**
 * 是否为小米手机
 */
fun isXiaomi(): Boolean {
    val flag = Build.MANUFACTURER
    LogUtil_d("Manufacturer", "手机厂商:$flag")
    return flag == "Xiaomi"
}

/**
 * 是否为华为手机
 */
fun isHuawei(): Boolean {
    val flag = Build.MANUFACTURER
    LogUtil_d("Manufacturer", "手机厂商:$flag")
    return flag == "HUAWEI"
}

/**
 * 是否为魅族手机
 */
fun isMeizu(): Boolean {
    val flag = Build.MANUFACTURER
    LogUtil_d("Manufacturer", "手机厂商:$flag")
    return flag == "Meizu"
}




