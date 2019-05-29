package com.ly.video.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.ly.pub.util.LogUtil_d
import com.ly.video.VideoManager
import com.ly.video.VideoMeasure
import java.io.File

/**
 * Created by LanYang on 2019/5/2
 *
 * 展示视频画面;参考悬浮窗口[com.ly.video.view.AbstractVideoView]
 */
class TextureRenderView : TextureView, TextureView.SurfaceTextureListener, IRenderView {

    private lateinit var mVideoMeasure: VideoMeasure
    private lateinit var mSurface: Surface

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
        surfaceTextureListener = this
    }

    override fun setRenderViewSize(videoWidth: Int, videoHeight: Int) {
        mVideoMeasure.setVideoSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun captureBitmap(callback: (bitmap: Bitmap) -> Unit) {
    }

    override fun captureGif(path: String, failure: () -> Unit, success: (file: File) -> Unit) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mVideoMeasure.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mVideoMeasure.getMeasuredWidth(), mVideoMeasure.getMeasuredHeight())
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        LogUtil_d(
            this.javaClass.simpleName,
            "onSurfaceTextureSizeChanged[surface=${surface.toString()} " +
                    "width=$width height=$height]"
        )
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        LogUtil_d(
            this.javaClass.simpleName,
            "onSurfaceTextureAvailable[[surface=${surface.toString()}] " +
                    "width=$width height=$height]"
        )

        if (this::mSurface.isInitialized) mSurface.release()
        mSurface = Surface(surface)
        VideoManager.videoPlayer.setSurface(mSurface)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureUpdated[surface=${surface.toString()}]")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureDestroyed[surface=${surface.toString()}]")

        if (this::mSurface.isInitialized) mSurface.release()
        return true
    }
}