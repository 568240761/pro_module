package com.ly.video.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.saveBitmap
import com.ly.pub.util.showToast
import com.ly.video.R
import com.ly.video.VideoManager
import com.ly.video.millisecondToHMS
import com.ly.video.player.IVideoPlayer
import kotlinx.android.synthetic.main.video_layout_default_video_view.view.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Created by LanYang on 2019/3/15
 * 默认实现的视频播放控件
 */
class DefaultVideoView : AbstractVideoView, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private lateinit var mProgressAnimation: ValueAnimator
    private lateinit var mHideAnimation: AnimatorSet
    private lateinit var mShowAnimation: AnimatorSet

    /**当前视频播放时间*/
    private var mCurrentTime: Long = 0

    /**隐藏与显示动画执行最长时间*/
    private val mDuration = 400L

    /**视频控制UI显示时长*/
    private val mShowDuration = 3000L

    /**是否在拖动进度条*/
    private var mTrackBar = false

    /**是否重新播放*/
    private var mReplay = false

    override fun getLayoutId(): Int {
        return R.layout.video_layout_default_video_view
    }

    override fun initView() {
        video_play.setOnClickListener(this)
        video_seekbar.setOnSeekBarChangeListener(this)
        video_screenshots.setOnClickListener(this)
        video_gif.setOnClickListener(this)
        video_layout.setOnClickListener(this)

        VideoManager.videoPlayer.setOnErrorListener {
            video_play.visibility = View.GONE
            video_error.text = it
            video_error.visibility = View.VISIBLE
        }

        VideoManager.videoPlayer.setOnCompletionListener {
            video_play.setImageResource(R.drawable.video_layer_restart)
            mReplay = true
            if (video_play.alpha == 0f) {
                showUiAnimator()
                playShowAnimation()
            } else {
                if (mHideAnimation.isStarted) {
                    mHideAnimation.cancel()
                }
            }
        }
    }

    override fun prepared() {
        if (mCurrentTime > 0) video_current.text = mCurrentTime.millisecondToHMS()
        video_duration.text = VideoManager.videoPlayer.getDuration().millisecondToHMS()
        video_seekbar.progress = initProgressAnimator()

        video_prepare.visibility = View.GONE
        video_play.visibility = View.VISIBLE
        video_layout_bottom.visibility = View.VISIBLE

        mProgressAnimation.start()

        post {
            hideUiAnimator()
            playHideAnimation()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video_play -> {
                if (VideoManager.videoPlayer.isPlaying()) {
                    VideoManager.videoPlayer.pause {
                        video_play.setImageResource(R.drawable.video_layer_start)

                        if (this::mHideAnimation.isInitialized) {
                            mHideAnimation.cancel()
                        }

                        if (this::mProgressAnimation.isInitialized)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                mProgressAnimation.pause()
                            } else {
                                mCurrentTime = VideoManager.videoPlayer.getCurrentPosition()
                                mProgressAnimation.end()
                                initProgressAnimator()
                            }
                    }
                } else if (VideoManager.videoPlayer.isPreparedState()) {
                    VideoManager.videoPlayer.start {
                        video_play.setImageResource(R.drawable.video_layer_pause)
                        if (mReplay) {
                            mReplay = false
                            mCurrentTime = VideoManager.videoPlayer.getCurrentPosition()
                            mProgressAnimation.end()
                            initProgressAnimator()
                            mProgressAnimation.start()
                        } else if (this::mProgressAnimation.isInitialized)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                mProgressAnimation.resume()
                            } else {
                                mProgressAnimation.start()
                            }

                        playHideAnimation()
                    }
                }
            }

            R.id.video_screenshots -> {
                render.captureBitmap {
                    val path =
                        Environment.getExternalStorageDirectory().path + "/DCIM/${PUBLIC_APPLICATION.packageName}"
                    val name = "${Date().time}.png"
                    saveBitmap(it, path, name)
                    post {
                        showToast("保存文件在/DCIM/${PUBLIC_APPLICATION.packageName}下！")
                    }
                }
            }

            R.id.video_gif -> {
                val path =
                    Environment.getExternalStorageDirectory().path + "/DCIM/${PUBLIC_APPLICATION.packageName}/"

                val name = "$path${Date().time}.gif"
                render.captureGif(
                    path = name,
                    handleCallback = {
                        showToast("正在生成GIF！")
                    },
                    failureCallback = {
                        showToast("生成GIF失败！")
                    },
                    successCallback = {
                        showToast("生成GIF成功！")
                    }
                )
            }

            R.id.video_layout -> {
                if (video_play.alpha == 0f) {
                    playShowAnimation()
                    playHideAnimation()
                }
            }
        }
    }

    private fun initProgressAnimator(): Int {
        val total = VideoManager.videoPlayer.getDuration()

        LogUtil_d(this.javaClass.simpleName, "cur=$mCurrentTime total=$total")
        val cur = BigDecimal(mCurrentTime)
            .divide(BigDecimal(total), 2, RoundingMode.HALF_UP)
            .multiply(BigDecimal("100")).toInt()
        LogUtil_d(this.javaClass.simpleName, "curInt=$cur")

        mProgressAnimation = ValueAnimator.ofInt(cur, 100)
        mProgressAnimation.addUpdateListener {
            if (!mTrackBar) {
                val value = it.animatedValue as Int
                video_seekbar.progress = value
                video_current.text = VideoManager.videoPlayer.getCurrentPosition().millisecondToHMS()
            }
        }
        mProgressAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                if (!mTrackBar) {
                    video_seekbar.progress = 100
                    video_current.text = VideoManager.videoPlayer.getDuration().millisecondToHMS()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        mProgressAnimation.interpolator = LinearInterpolator()
        mProgressAnimation.duration = total - mCurrentTime

        return cur
    }

    private fun hideUiAnimator() {
        val playAnim = ObjectAnimator.ofFloat(video_play, "alpha", 1f, 0f)
        val topAnim = ObjectAnimator.ofFloat(
            video_layout_top,
            "translationY",
            0f,
            (video_layout_top.top - video_layout_top.bottom).toFloat()
        )
        val bottomAnim = ObjectAnimator.ofFloat(
            video_layout_bottom,
            "translationY",
            0f,
            (video_layout_bottom.bottom - video_layout_bottom.top).toFloat()
        )

        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : Animator.AnimatorListener {
            private var mIsCancel = false
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                LogUtil_d(this@DefaultVideoView.javaClass.simpleName, "隐藏动画结束")
                video_play.visibility = View.GONE
                showUiAnimator()
                if (mIsCancel) {
                    mIsCancel = false
                    val duration = mDuration * (1f - video_play.alpha)
                    mShowAnimation.duration = duration.toLong()
                    playShowAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
                LogUtil_d(this@DefaultVideoView.javaClass.simpleName, "取消成功")
                mIsCancel = true
            }

            override fun onAnimationStart(animation: Animator?) {
                LogUtil_d(this@DefaultVideoView.javaClass.simpleName, "隐藏动画开始")
            }
        })
        animatorSet.play(topAnim).with(bottomAnim).with(playAnim)
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = mDuration
        animatorSet.startDelay = mShowDuration
        mHideAnimation = animatorSet
    }

    private fun playHideAnimation() {
        mHideAnimation.start()
    }

    private fun showUiAnimator() {
        val playAnim = ObjectAnimator.ofFloat(video_play, "alpha", video_play.alpha, 1f)

        val topAnim = ObjectAnimator.ofFloat(
            video_layout_top,
            "translationY",
            video_layout_top.translationY,
            0f
        )
        val bottomAnim = ObjectAnimator.ofFloat(
            video_layout_bottom,
            "translationY",
            video_layout_bottom.translationY,
            0f
        )

        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                video_play.visibility = View.VISIBLE
            }
        })
        animatorSet.play(topAnim).with(bottomAnim).with(playAnim)
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = mDuration
        mShowAnimation = animatorSet
    }

    private fun playShowAnimation() {
        mShowAnimation.start()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (mTrackBar) {
            LogUtil_d(this.javaClass.simpleName, "onProgressChanged[progress=$progress]")
            mCurrentTime = BigDecimal(progress)
                .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal(VideoManager.videoPlayer.getDuration())).toLong()
            video_current.text = mCurrentTime.millisecondToHMS()
            if (mReplay) video_play.setImageResource(R.drawable.video_layer_start)
            LogUtil_d(this.javaClass.simpleName, "onProgressChanged[curDuration=$mCurrentTime]")
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        LogUtil_d(this.javaClass.simpleName, "onStartTrackingTouch")
        mTrackBar = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        LogUtil_d(this.javaClass.simpleName, "onStopTrackingTouch")
        VideoManager.videoPlayer.seekTo(mCurrentTime)
        mTrackBar = false
        if (VideoManager.videoPlayer.isPlaying()) {
            mProgressAnimation.end()
            initProgressAnimator()
            mProgressAnimation.start()
        }
    }

    /**
     * 设置视频显示信息
     * @param title 视频名称
     * @param current 视频已播放时长
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     * @param operation 响应视频播放开始或停止,UI的回调接口
     * @param click 点击左上方返回按钮回调
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    fun init(
        title: String,
        current: Long = 0L,
        uri: Uri,
        headers: Map<String, String>? = null,
        operation: IVideoPlayer.IUIOperatorListener? = null,
        click: OnClickListener? = null,
        isDebug: Boolean
    ) {
        video_title.text = title
        mCurrentTime = current
        video_back.setOnClickListener {
            click?.onClick(it)
        }
        initData(uri, headers, operation, isDebug)
    }
}