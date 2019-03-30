package com.ly.video.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d
import com.ly.video.HoverCallback
import com.ly.video.R
import com.ly.video.checkHoverPermission
import com.ly.video.hover.HoverManager
import com.ly.video.hover.HoverOption
import com.ly.video.millisecondToHMS
import com.ly.video.player.IChangeUIListener
import com.ly.video.player.VideoPlayerManager
import com.ly.video.player.VideoPlayerOption
import com.ly.video.render.SurfaceRenderView
import kotlinx.android.synthetic.main.video_layout_player.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/3/15
 */
class LYVideoView : FrameLayout, View.OnClickListener, DefaultLifecycleObserver {

    private val MSG_TIME_WHAT = 1

    private var mIsCanHandler = false

    private val mTimeHandler = @SuppressLint("HandlerLeak") object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                MSG_TIME_WHAT -> {
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
                        sendEmptyMessageDelayed(MSG_TIME_WHAT, 1000)
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
    lateinit var render: SurfaceRenderView

    /**返回按钮*/
    lateinit var goBack: ImageView

    /**标题*/
    lateinit var title: TextView

    /**更多功能*/
    lateinit var moreFunc: ImageView

    /**播放状态*/
    lateinit var playStatus: ImageView

    /**可拖动进度条*/
    lateinit var seekbar: SeekBar

    /**视频已播放时长*/
    lateinit var current: TextView

    /**视频总时长*/
    lateinit var duration: TextView

    private fun loadView() {
        LayoutInflater.from(context).inflate(R.layout.video_layout_player, this, true)
        render = video_render
        goBack = video_back
        title = video_title
        moreFunc = video_more
        playStatus = video_play
        seekbar = video_seekbar
        current = video_current
        duration = video_duration

        if (context is LifecycleOwner)//context必须为[AppCompatActivity]的实例,[LYVideoView]才能响应到[AppCompatActivity]的生命周期
            (context as LifecycleOwner).lifecycle.addObserver(this)
        else
            throw  IllegalAccessException("[LYVideoView]实例中的[context]必须为[AppCompatActivity]的实例")
    }

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
        playStatus.setOnClickListener(this)

        val option = VideoPlayerOption().setUri(uri)
            .setDebug(isDebug)
            .setHeader(headers)
            .setRender(render)
        VideoPlayerManager.createVideoPlayer(option)

        VideoPlayerManager.getIVideoPlayer().clearListeners()
        VideoPlayerManager.getIVideoPlayer().setOnPreparedListener(IMediaPlayer.OnPreparedListener {
            playStatus.visibility = View.VISIBLE
            duration.text = millisecondToHMS(VideoPlayerManager.getIVideoPlayer().getDuration())
        })
        VideoPlayerManager.getIVideoPlayer().setOnCompletionListener(IMediaPlayer.OnCompletionListener {
            playStatus.setImageResource(R.drawable.video_layer_start)
            stopHandleMessage()
            changeCurrentPosition(
                VideoPlayerManager.getIVideoPlayer().getDuration(),
                VideoPlayerManager.getIVideoPlayer().getDuration()
            )
        })
        VideoPlayerManager.getIVideoPlayer().setOnErrorListener()
        VideoPlayerManager.getIVideoPlayer().setIChangeUIListener(object :IChangeUIListener{
            override fun startCauseUI() {
                startHandleMessage()
                playStatus.setImageResource(R.drawable.video_layer_pause)
            }

            override fun pauseCauseUI() {
                stopHandleMessage()
                playStatus.setImageResource(R.drawable.video_layer_start)
            }
        })

        VideoPlayerManager.getIVideoPlayer().prepareAsync()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video_play -> {
                if (VideoPlayerManager.getIVideoPlayer().isPreparedState()) {
                    if (VideoPlayerManager.getIVideoPlayer().isPlaying()) {
                        VideoPlayerManager.getIVideoPlayer().pause()
                    } else {
                        VideoPlayerManager.getIVideoPlayer().start()
                    }
                }
            }
        }
    }

    private fun startHandleMessage() {
        mIsCanHandler = true
        mTimeHandler.sendEmptyMessageDelayed(MSG_TIME_WHAT, 1000)
    }

    private fun stopHandleMessage() {
        mIsCanHandler = false
        mTimeHandler.removeMessages(MSG_TIME_WHAT)
    }

    private fun changeCurrentPosition(currentPosition: Long, duration: Long) {
        current.text = millisecondToHMS(currentPosition)
        seekbar.progress = (currentPosition.toFloat() / duration * 100).toInt()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this@LYVideoView.javaClass.simpleName, "onDestroy")
        stopHandleMessage()

        //检查是否拥有悬浮权限
        checkHoverPermission(this@LYVideoView.context, object : HoverCallback {
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