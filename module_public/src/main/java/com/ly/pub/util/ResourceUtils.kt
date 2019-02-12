package com.ly.pub.util

import android.util.TypedValue
import androidx.annotation.StringRes
import com.ly.pub.PUBLIC_APPLICATION

/**
 * Created by LanYang on 2018/11/1
 */

/**
 * 返回字符串资源id对应的字符串
 * @param id 字符串资源id
 * @return 字符串
 */
fun retString(@StringRes id: Int): String {
    return PUBLIC_APPLICATION.resources.getString(id)
}


fun retDensity(): Float {
    return PUBLIC_APPLICATION.resources.displayMetrics.density
}


fun retDensityDPI(): Int {
    return PUBLIC_APPLICATION.resources.displayMetrics.densityDpi
}

/**
 * 返回屏幕宽度，单位px
 */
fun retScreenWidthPx(): Int {
    return PUBLIC_APPLICATION.resources.displayMetrics.widthPixels
}

/**
 * 返回屏幕高度，单位px
 */
fun retScreenHeightPx(): Int {
    return PUBLIC_APPLICATION.resources.displayMetrics.heightPixels
}

/**
 * 返回屏幕宽度，单位dp
 */
fun retScreenWidthDp(): Int {
    return retPx2dip(retScreenWidthPx().toFloat())
}

/**
 * 返回屏幕高度，单位dp
 */
fun retScreenHeightDp(): Int {
    return retPx2dip(retScreenHeightPx().toFloat())
}

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
fun retDip2px(dpValue: Float): Int {
    val scale = retDensity()
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun retPx2dip(pxValue: Float): Int {
    val scale = retDensity()
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 获取状态栏高度
 */
fun retStatusHeight(): Int {
    var result = 0
    val resourceId = PUBLIC_APPLICATION.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = PUBLIC_APPLICATION.resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * 获取ActionBar高度
 */
fun retActionBarHeight(): Int {
    var actionBarHeight = 0
    val value = TypedValue()
    if (PUBLIC_APPLICATION.theme.resolveAttribute(android.R.attr.actionBarSize, value, true)) {
        actionBarHeight = TypedValue.complexToDimensionPixelSize(value.data, PUBLIC_APPLICATION.resources.displayMetrics)
    }
    return actionBarHeight
}

/**
 * 获取导航栏高度
 */
fun retNavigationBarHeight(): Int {
    var result = 0
    val show = PUBLIC_APPLICATION.resources.getIdentifier("config_showNavigationBar", "bool", "android")
    if (show != 0) {
        val resourceId = PUBLIC_APPLICATION.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = PUBLIC_APPLICATION.resources.getDimensionPixelSize(resourceId)
        }
    }
    return result
}