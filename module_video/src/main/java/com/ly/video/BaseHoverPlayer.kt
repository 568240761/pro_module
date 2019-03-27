package com.ly.video

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.ly.pub.PUBLIC_APPLICATION

/**
 * Created by LanYang on 2019/3/27
 * 悬浮播放器
 */
class BaseHoverPlayer : IHoverPlayer {

    private val mWindowManager = PUBLIC_APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val mLayoutParams = WindowManager.LayoutParams()

    private val mView: View

    private val mPlayer: IVideoPlayer

    private val mPlayStatus: ImageView

    init {
        val inflate = PUBLIC_APPLICATION.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflate.inflate(R.layout.video_layout_hover_player, null)
        mPlayer = mView.findViewById<BaseVideoPlayer>(R.id.hover_player)
        mPlayStatus = mView.findViewById(R.id.hover_status)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        mLayoutParams.type = type
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams.windowAnimations = 0
        mLayoutParams.format = PixelFormat.RGBA_8888
    }

    override fun setSize(width: Int, height: Int) {
        mLayoutParams.width = width
        mLayoutParams.height = height
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        mLayoutParams.gravity = gravity
        mLayoutParams.x = xOffset
        mLayoutParams.y = yOffset
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun updatePosition(x: Int, y: Int) {
        mLayoutParams.x = x
        mLayoutParams.y = y
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun show() {
        mWindowManager.addView(mView,mLayoutParams)
    }

    override fun destroy() {
        mWindowManager.removeViewImmediate(mView)
    }
}