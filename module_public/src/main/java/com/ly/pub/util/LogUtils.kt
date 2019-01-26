package com.ly.pub.util

import android.util.Log
import com.ly.pub.PUBLIC_IS_LOG

/**
 * Created by LanYang on 2019/1/26
 */

fun LogUtil_i(tag: String, msg: String) {
    if (PUBLIC_IS_LOG) Log.i(tag, msg)
}

fun LogUtil_d(tag: String, msg: String) {
    if (PUBLIC_IS_LOG) Log.d(tag, msg)
}

fun LogUtil_e(tag: String, msg: String) {
    if (PUBLIC_IS_LOG) Log.e(tag, msg)
}

fun LogUtil_e(e: Exception) {
    if (PUBLIC_IS_LOG) e.printStackTrace()
}