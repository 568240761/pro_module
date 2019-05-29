package com.ly.video.render

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import com.ly.pub.util.LogUtil_d
import com.ly.video.VideoManager
import com.ly.video.VideoMeasure
import java.io.File

/**
 * Created by LanYang on 2019/3/4
 *
 * 展示视频画面;参考悬浮窗口[com.ly.video.suspension.AbstractSusWindow]
 */
class SurfaceRenderView : SurfaceView, SurfaceHolder.Callback, IRenderView {

    private lateinit var mVideoMeasure: VideoMeasure

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
        mVideoMeasure = VideoMeasure(this)
        holder.addCallback(this)
    }

    override fun setRenderViewSize(videoWidth: Int, videoHeight: Int) {
        mVideoMeasure.setVideoSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun captureBitmap(callback: (bitmap: Bitmap) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "do not support captureBitmap")
    }

    override fun captureGif(path: String, failure: () -> Unit, success: (file: File) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "do not support captureGif")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mVideoMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mVideoMeasure.getMeasuredWidth(), mVideoMeasure.getMeasuredHeight())
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        LogUtil_d(this.javaClass.simpleName, "surfaceCreated[holder=${holder.toString()}]")

        VideoManager.videoPlayer.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        LogUtil_d(
            this.javaClass.simpleName,
            "surfaceChanged[holder=${holder.toString()};format=$format;width=$width;height=$height]"
        )
    }


    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        LogUtil_d(this.javaClass.simpleName, "surfaceDestroyed[holder=${holder.toString()}]")

        holder?.surface?.release()
    }
}