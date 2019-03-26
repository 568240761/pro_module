package com.ly.video

import android.annotation.SuppressLint
import com.ly.pub.util.LogUtil_e
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by LanYang on 2019/03/25.
 * Email:568240761@qq.com
 */

/**
 * 将毫秒转成时分秒
 * @param milliseconds 毫秒
 */
@SuppressLint("SimpleDateFormat")
fun millisecondToHMS(milliseconds: Long): String {
    var str = "0:00"
    if (milliseconds > 0L) {
        val format = SimpleDateFormat("HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("GMT+00:00")
        try {
            str = format.format(milliseconds)
            return when {
                str.startsWith("00:0") -> str.removeRange(0, 4)
                str.startsWith("00:") -> str.removeRange(0, 3)
                str.startsWith("0") -> str.removeRange(0, 1)
                else -> str
            }
        } catch (e: Exception) {
            LogUtil_e(e)
        }
    }
    return str
}