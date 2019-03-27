package com.ly.video

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_i
import kotlinx.android.synthetic.main.video_layout_player.view.*
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/3/15
 */
class LYVideoPlayer : BaseVideoPlayer, View.OnClickListener {

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
        goBack = video_back
        title = video_title
        moreFunc = video_more
        playStatus = video_play
        seekbar = video_seekbar
        current = video_current
        duration = video_duration

        playStatus.setOnClickListener(this)

        setOnPreparedListener(IMediaPlayer.OnPreparedListener { playStatus.visibility = View.VISIBLE })

        setOnCompletionListener(IMediaPlayer.OnCompletionListener { playStatus.setImageResource(R.drawable.video_start) })

        setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener { _, percent: Int ->
            LogUtil_i(this@LYVideoPlayer.javaClass.simpleName, "percent=$percent")
        })

        setOnTimedTextListener(IMediaPlayer.OnTimedTextListener { _, ijkTimedText ->
            LogUtil_i(this@LYVideoPlayer.javaClass.simpleName, "text=${ijkTimedText.text}")
        })

        setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener {
            LogUtil_i(this@LYVideoPlayer.javaClass.simpleName, "seek完成！")
        })

        setOnInfoListener(IMediaPlayer.OnInfoListener { iMediaPlayer: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_i(this@LYVideoPlayer.javaClass.simpleName, "what=$what extra=$extra")
            false
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.video_play -> {
                if (isPreparedState()) {
                    if (mMediaPlayer.isPlaying) {
                        pause()
                        playStatus.setImageResource(R.drawable.video_start)
                    } else {
                        start()
                        playStatus.setImageResource(R.drawable.video_pause)
                    }
                }
            }
        }
    }

    override fun changeCurrentPosition(currentPosition: Long, duration: Long) {
        LogUtil_d(this@LYVideoPlayer.javaClass.simpleName, "cur=$currentPosition dur=$duration")
        current.text = millisecondToHMS(currentPosition)
        seekbar.progress = (currentPosition.toFloat() / duration * 100).toInt()
    }
}