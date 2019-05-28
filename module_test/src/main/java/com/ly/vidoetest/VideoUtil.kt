package com.ly.vidoetest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
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

/**
 * 检查悬浮权限(Android版本大于23)
 * @param context 上下文环境
 * @param callback 检查结果回调
 */
fun checkHoverPermission(context: Context, callback: HoverCallback) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        callback.success()
        return
    }

    if (!Settings.canDrawOverlays(context)) {
        VideoHoverActivity.callback = callback
        val intent = Intent(context, VideoHoverActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } else {
        callback.success()
    }
}

class VideoHoverActivity : AppCompatActivity() {

    companion object {
        var callback: HoverCallback? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        requestAlertWindowPermission()
    }

    private fun requestAlertWindowPermission() {
        val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION")
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                callback?.success()
            } else {
                callback?.fail()
            }
        }
        callback = null
        finish()
    }
}

interface HoverCallback {
    fun success()
    fun fail()
}