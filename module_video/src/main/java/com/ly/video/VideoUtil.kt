package com.ly.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_e
import com.ly.pub.util.getAllSuperClass
import com.ly.video.suspension.ISusWindow
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by LanYang on 2019/5/28
 */

/**
 * 将毫秒转成时分秒
 */
@SuppressLint("SimpleDateFormat")
fun Long.millisecondToHMS(): String {
    var str = "0:00"
    if (this > 0L) {
        val format = SimpleDateFormat("HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("GMT+00:00")
        try {
            str = format.format(this)
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
 * 检查该类是否为ISusWindow的子类;
 *
 * 是ISusWindow的子类,返回true;否则,返回false
 */
fun Class<*>.checkSusWindow(): Boolean {
    val list = getAllSuperClass(this)
    for (index in list.indices) {
        LogUtil_d("Class<*>.checkSusWindow", "superClass=${list[index].name}")

        if (list[index].name == ISusWindow::class.java.name) break

        if (index + 1 == list.size) return false
    }

    return true
}

/**
 * 返回截取图片质量参数
 */
fun Int.getCaptureConfig(): Bitmap.Config {
    return when (this) {
        0 -> Bitmap.Config.ALPHA_8
        1 -> Bitmap.Config.RGB_565
        else -> Bitmap.Config.ARGB_8888
    }
}

/**
 * 检查悬浮权限(Android版本大于23)
 *
 * @param callback 检查结果回调
 */
fun Context.checkHoverPermission(callback: HoverCallback) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        callback.success()
        return
    }

    if (!Settings.canDrawOverlays(this)) {
        VideoHoverActivity.callback = callback
        val intent = Intent(this, VideoHoverActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
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


/**
 * 用于计算[com.ly.video.render.IRenderView]宽高
 */
internal class VideoMeasure(view: View) {

    private var mWeakView: WeakReference<View>? = null

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0

    private var mMeasuredWidth: Int = 0
    private var mMeasuredHeight: Int = 0

    init {
        mWeakView = WeakReference(view)
    }

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoWidth = videoWidth
        mVideoHeight = videoHeight
    }

    fun getMeasuredWidth(): Int {
        return mMeasuredWidth
    }

    fun getMeasuredHeight(): Int {
        return mMeasuredHeight
    }

    fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(mVideoHeight, heightMeasureSpec)
        LogUtil_d(this.javaClass.simpleName, "Width=$width-Height=$height")
        LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$mVideoWidth-mVideoHeight=$mVideoHeight")

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            val factor = mVideoWidth.toFloat() / mVideoHeight
            val tempHeight = (width.toFloat() / factor).toInt()
            if (tempHeight > height) {
                val tempWidth = (height * factor).toInt()
                if (tempWidth <= width) {
                    width = tempWidth
                    LogUtil_d(this.javaClass.simpleName, "以高为基准[tempWidth=$tempWidth-Height=$height]")
                }
            } else {
                height = tempHeight
                LogUtil_d(this.javaClass.simpleName, "以宽为基准[Width=$width-tempHeight=$tempHeight]")
            }
        } else {
            LogUtil_d(this.javaClass.simpleName, "未获取视频真实宽高")
        }

        mMeasuredWidth = width
        mMeasuredHeight = height
    }
}