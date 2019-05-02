package com.ly.video.player

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ly.video.R
import com.ly.video.millisecondToHMS
import kotlinx.android.synthetic.main.video_layout_player.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/3/15
 * 默认实现的视频播放控件
 */
class LYVideoView : BaseVideoView {

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

    /**返回按钮*/
    private lateinit var mGoBack: ImageView

    /**标题*/
    private lateinit var mTitle: TextView

    /**更多功能*/
    private lateinit var mMoreFunc: ImageView

    /**播放状态*/
    private lateinit var mPlayStatus: ImageView

    /**可拖动进度条*/
    private lateinit var mSeekbar: SeekBar

    /**视频已播放时长*/
    private lateinit var mCurrent: TextView

    /**视频总时长*/
    private lateinit var mDuration: TextView


    override fun getLayoutId(): Int {
        return R.layout.video_layout_player
    }

    override fun initView() {
        mGoBack = video_back
        mTitle = video_title
        mMoreFunc = video_more
        mPlayStatus = video_play
        mSeekbar = video_seekbar
        mCurrent = video_current
        mDuration = video_duration

        mPlayStatus.setOnClickListener {
            if (VideoPlayerManager.getIVideoPlayer().isPreparedState()) {
                if (VideoPlayerManager.getIVideoPlayer().isPlaying()) {
                    VideoPlayerManager.getIVideoPlayer().pause()
                } else {
                    VideoPlayerManager.getIVideoPlayer().start()
                }
            }
        }
        mMoreFunc.setOnClickListener {
            VideoPlayerManager.getIVideoPlayer().captureFrame()
        }
    }

    /**
     * 设置视频显示信息
     * @param title 视频名称
     * @param current 视频已播放时长
     */
    fun initViewData(title: String, current: Long = 0L) {
        mTitle.text = title
        if (current > 0) mCurrent.text = millisecondToHMS(current)

    }

    /**
     * 点击左上方返回按钮回调
     */
    fun setGoBackEvent(goBackClick: View.OnClickListener) {
        mGoBack.setOnClickListener {
            goBackClick.onClick(it)
        }
    }

    override fun initVideoPlayerListener() {
        VideoPlayerManager.getIVideoPlayer().setOnPreparedListener(IMediaPlayer.OnPreparedListener {
            mPlayStatus.visibility = View.VISIBLE
            mDuration.text = millisecondToHMS(VideoPlayerManager.getIVideoPlayer().getDuration())
        })
        VideoPlayerManager.getIVideoPlayer().setOnCompletionListener(IMediaPlayer.OnCompletionListener {
            mPlayStatus.setImageResource(R.drawable.video_layer_start)
            stopHandleMessage()
            changeCurrentPosition(
                VideoPlayerManager.getIVideoPlayer().getDuration(),
                VideoPlayerManager.getIVideoPlayer().getDuration()
            )
        })
        VideoPlayerManager.getIVideoPlayer().setOnErrorListener()
    }

    override fun startHandleMessage() {
        super.startHandleMessage()
        mPlayStatus.setImageResource(R.drawable.video_layer_pause)
    }

    override fun stopHandleMessage() {
        super.stopHandleMessage()
        mPlayStatus.setImageResource(R.drawable.video_layer_start)
    }

    override fun changeCurrentPosition(currentPosition: Long, duration: Long) {
        mCurrent.text = millisecondToHMS(currentPosition)
        mSeekbar.progress = (currentPosition.toFloat() / duration * 100).toInt()
    }
}