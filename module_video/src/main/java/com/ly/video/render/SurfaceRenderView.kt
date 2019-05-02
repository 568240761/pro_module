package com.ly.video.render

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.RequiresApi
import com.ly.pub.util.LogUtil_d
import com.ly.video.VideoMeasureUtil
import com.ly.video.player.VideoPlayerManager

/**
 * Created by LanYang on 2019/3/4
 */

class SurfaceRenderView : SurfaceView,SurfaceHolder.Callback,IRenderView {

    private lateinit var mVideoMeasureUtil: VideoMeasureUtil

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        initView()
    }

    private fun initView() {
        mVideoMeasureUtil = VideoMeasureUtil(this)
        holder.addCallback(this)
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoMeasureUtil.setVideoSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mVideoMeasureUtil.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mVideoMeasureUtil.getMeasuredWidth(), mVideoMeasureUtil.getMeasuredHeight())
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        LogUtil_d(this.javaClass.simpleName, "surfaceCreated")
        VideoPlayerManager.getIVideoPlayer().setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        LogUtil_d(
            this.javaClass.simpleName,
            "surfaceChanged[holder=${holder.toString()};format=$format;width=$width;height=$height]"
        )
    }


    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        LogUtil_d(this.javaClass.simpleName, "surfaceDestroyed")
    }
}