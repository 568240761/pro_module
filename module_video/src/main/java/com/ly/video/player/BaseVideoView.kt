package com.ly.video.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d
import com.ly.video.HoverCallback
import com.ly.video.checkHoverPermission
import com.ly.video.hover.BaseHover
import com.ly.video.hover.HoverManager
import com.ly.video.hover.HoverOption
import com.ly.video.render.IRenderView
import com.ly.video.render.SurfaceRenderView

/**
 * Created by LanYang on 2019/3/30
 * 视频播放控件;默认使用有UI界面的视频播放控件[LYVideoView],可以通过继承[BaseVideoView]类来实现自定义UI界面的视频播放控件
 */
private const val MSG_WHAT_VIDEO_CURRENT = 1

abstract class BaseVideoView : FrameLayout, DefaultLifecycleObserver {

    private var mIsCanHandler = false

    private val mTimeHandler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_WHAT_VIDEO_CURRENT -> {
                    if (!mIsCanHandler) {
                        return
                    }

                    if (context is Activity) {
                        if ((context as Activity).isFinishing) return
                    }

                    changeCurrentPosition(
                            VideoPlayerManager.getIVideoPlayer().getCurrentPosition(),
                            VideoPlayerManager.getIVideoPlayer().getDuration()
                    )

                    if (VideoPlayerManager.getIVideoPlayer().getCurrentPosition() < VideoPlayerManager.getIVideoPlayer().getDuration())
                        sendEmptyMessageDelayed(MSG_WHAT_VIDEO_CURRENT, 1000)
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
        loadView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        loadView()
    }

    /**画面渲染*/
    private lateinit var mRender: IRenderView

    private fun loadView() {
        val surfaceRenderView = SurfaceRenderView(context)
        surfaceRenderView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER)
        addView(surfaceRenderView)
        mRender = surfaceRenderView

        LayoutInflater.from(context).inflate(getLayoutId(), this, true)

        initView()

        if (context is LifecycleOwner)//context必须为[AppCompatActivity]的实例,[LYVideoView]才能响应到[AppCompatActivity]的生命周期
            (context as LifecycleOwner).lifecycle.addObserver(this)
        else
            throw  IllegalAccessException("[LYVideoView]实例中的[context]必须为[AppCompatActivity]的实例")
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    /**
     * 初始化数据
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    fun initData(
            uri: Uri,
            headers: Map<String, String>? = null,
            isDebug: Boolean
    ) {
        val option = VideoPlayerOption().setUri(uri)
                .setDebug(isDebug)
                .setHeader(headers)
                .setRender(mRender)
        VideoPlayerManager.createVideoPlayer(option)

        VideoPlayerManager.getIVideoPlayer().clearListeners()

        initVideoPlayerListener()

        VideoPlayerManager.getIVideoPlayer().setIChangeUIListener(object : IChangeUIListener {
            override fun startCauseUI() {
                startHandleMessage()
            }

            override fun pauseCauseUI() {
                stopHandleMessage()
            }
        })

        VideoPlayerManager.getIVideoPlayer().prepareAsync()
    }

    protected abstract fun initVideoPlayerListener()

    open fun startHandleMessage() {
        mIsCanHandler = true
        mTimeHandler.sendEmptyMessageDelayed(MSG_WHAT_VIDEO_CURRENT, 1000)
    }

    open fun stopHandleMessage() {
        mIsCanHandler = false
        mTimeHandler.removeMessages(MSG_WHAT_VIDEO_CURRENT)
    }

    abstract fun changeCurrentPosition(currentPosition: Long, duration: Long)

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this@BaseVideoView.javaClass.simpleName, "onDestroy")
        stopHandleMessage()

        //检查是否拥有悬浮权限
        checkHoverPermission(this@BaseVideoView.context, object : HoverCallback {
            override fun success() {
                //视频正在播放,则打开悬浮窗口
                if (VideoPlayerManager.getIVideoPlayer().isPlaying()) {
                    val option = HoverOption()
                    HoverManager.createHoverPlayer(option)
                    HoverManager.show()
                }
            }

            override fun fail() {
                VideoPlayerManager.destroyVideoPlayer()
            }
        })
    }
}