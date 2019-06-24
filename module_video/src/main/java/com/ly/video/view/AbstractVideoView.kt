package com.ly.video.view

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d
import com.ly.video.VideoManager
import com.ly.video.player.IVideoPlayer
import com.ly.video.render.IRenderView
import com.ly.video.render.TextureRenderView

/**
 * Created by LanYang on 2019/5/29
 */
abstract class AbstractVideoView : FrameLayout, DefaultLifecycleObserver {

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

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun prepared()

    /**画面渲染*/
    protected lateinit var render: IRenderView

    private fun loadView() {
        val textureRenderView = TextureRenderView(context)
        textureRenderView.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        addView(textureRenderView)
        render = textureRenderView

        LayoutInflater.from(context).inflate(getLayoutId(), this, true)

        initView()

        //context必须为[AppCompatActivity]的实例,[AbstractVideoView]才能响应到[AppCompatActivity]的生命周期
        if (context is LifecycleOwner)
            (context as LifecycleOwner).lifecycle.addObserver(this)
    }

    /**
     * 初始化数据
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     * @param operation 响应视频播放开始或停止,UI的回调接口
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    protected fun initData(
        uri: Uri,
        headers: Map<String, String>? = null,
        operation: IVideoPlayer.IUIOperatorListener?,
        isDebug: Boolean
    ) {
        VideoManager.videoPlayer.setDebug(isDebug)
        VideoManager.videoPlayer.setOnlyOperatorListener(operation)
        VideoManager.videoPlayer.setVideoURI(uri, headers)
        VideoManager.videoPlayer.prepareAsync(
            prepared = { width, height ->
                render.setRenderViewSize(width, height)
                prepared()
            }
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onDestroy")

//        if (VideoManager.isShowSusWindow()) {
//            context.checkHoverPermission(object : HoverCallback {
//                override fun success() {
//                    VideoManager.videoPlayer.clearAllListener()
//                    showSusWindow()
//                }
//
//                override fun fail() {
//                    release()
//                }
//            })
//        } else {
//            release()
//        }
        release()
    }

    private fun release() {
        post {
            VideoManager.videoPlayer.clearAllListener()
            VideoManager.videoPlayer.pause()
            VideoManager.videoPlayer.stop()
            VideoManager.videoPlayer.release()
        }
    }
}

