package com.ly.video.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.ly.gif.generateVideoGif
import com.ly.pub.time.PubTimer
import com.ly.pub.util.LogUtil_d
import com.ly.video.VideoManager
import com.ly.video.VideoMeasure
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

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
        Thread {
            val bitmap = getBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
            callback(bitmap)
        }.start()
    }

    override fun captureGif(
        path: String,
        handleCallback: () -> Unit,
        failureCallback: () -> Unit,
        successCallback: (file: File) -> Unit
    ) {
        val bitmapList = ArrayList<Bitmap>()

        val time = BigDecimal("1000").divide(BigDecimal(VideoManager.getGifFps()), 0, RoundingMode.HALF_DOWN).toInt()
        LogUtil_d(this.javaClass.simpleName, "time=$time")

        val size = BigDecimal(VideoManager.getGifTotalTime()).divide(
            BigDecimal("1000"),
            0,
            RoundingMode.HALF_DOWN
        ).toInt() * VideoManager.getGifFps()
        LogUtil_d(this.javaClass.simpleName, "size=$size")

        val timer = PubTimer(context)
        val timerTask = object : TimerTask() {
            override fun run() {
                val bitmap = getBitmap(Bitmap.createBitmap(width, height, VideoManager.getCapGifConfig()))
                bitmapList.add(bitmap)
                LogUtil_d(this@TextureRenderView.javaClass.simpleName, "cur=${bitmapList.size}")

                if (bitmapList.size == size) {
                    timer.stop()
                    post { handleCallback() }
                    generateVideoGif(
                        bitmapList,
                        path,
                        fps = VideoManager.getGifFps().toFloat(),
                        failure = {
                            bitmapList.clear()
                            LogUtil_d(this@TextureRenderView.javaClass.simpleName, "生成GIF失败")
                            post { failureCallback() }
                        },
                        success = {
                            bitmapList.clear()
                            LogUtil_d(this@TextureRenderView.javaClass.simpleName, "生成GIF成功")
                            post { successCallback(it) }
                        })
                }
            }
        }

        timer.start(time = time.toLong(), timerTask = timerTask)
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
//        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureUpdated[surface=${surface.toString()}]")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureDestroyed[surface=${surface.toString()}]")

        if (this::mSurface.isInitialized) mSurface.release()
        return true
    }
}