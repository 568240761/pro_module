package com.ly.vidoetest.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.os.Build
import android.os.Environment
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import androidx.annotation.RequiresApi
import com.ly.gif.generateGif
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.time.PubCountDownTimer
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.showToast
import com.ly.vidoetest.VideoMeasureUtil
import com.ly.vidoetest.player.ICaptureFrame
import com.ly.vidoetest.player.VideoPlayerManager
import java.util.*

/**
 * Created by LanYang on 2019/5/2
 */
class TextureRenderView : TextureView, TextureView.SurfaceTextureListener, IRenderView {
    private lateinit var mVideoMeasureUtil: VideoMeasureUtil

    private val mBitmapList = ArrayList<Bitmap>()

    private var count=0

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
        surfaceTextureListener = this
    }

    override fun captureFrame(capture: ICaptureFrame) {
//        Thread {
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            val frame = getBitmap(bitmap)
//            capture.captureSuccess(frame)
//        }.start()

        val countDownTimer = PubCountDownTimer(5000L, 16L)
        countDownTimer.start(
            tick = { _: Long ->
                Thread {
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
                    val frame = getBitmap(bitmap)
                    mBitmapList.add(frame)
                    count+=1
                    LogUtil_d("GIF","count=$count")
                }.start()
            },
            finish = {
                Thread {
                    val path =
                        Environment.getExternalStorageDirectory().path + "/DCIM/${PUBLIC_APPLICATION.packageName}/"

                    val name = "$path${Date().time}.gif"
                    generateGif(
                        mBitmapList,
                        name,
                        fps=60f,
                        failure = {
                            mBitmapList.clear()
                            post { showToast("GIF文件生成失败") }
                        },
                        success = {
                            mBitmapList.clear()
                            post { showToast("GIF文件生成成功") }
                        })
                }.start()
            })
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoMeasureUtil.setVideoSize(videoWidth, videoHeight)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mVideoMeasureUtil.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mVideoMeasureUtil.getMeasuredWidth(), mVideoMeasureUtil.getMeasuredHeight())
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureSizeChanged[width=$width height=$height]")
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureUpdated")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureDestroyed")
        surface?.release()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        LogUtil_d(this.javaClass.simpleName, "onSurfaceTextureAvailable[width=$width height=$height]")
        VideoPlayerManager.getIVideoPlayer().setSurface(Surface(surface))
    }


}