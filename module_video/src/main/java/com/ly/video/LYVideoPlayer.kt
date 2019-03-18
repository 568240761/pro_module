package com.ly.video

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.video_layout_player.view.*

/**
 * Created by LanYang on 2019/3/15
 */
class LYVideoPlayer : BaseVideoPlayer {

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
    var goBack: ImageView? = null

    /**标题*/
    var title: TextView? = null

    /**更多功能*/
    var moreFunc: TextView? = null

    /**播放状态*/
    var playStatus: ImageView? = null

    /**可拖动进度条*/
    var seekbar: SeekBar? = null

    /**视频已播放时长*/
    var current: TextView? = null

    /**视频总时长*/
    var duration: TextView? = null

    private fun loadView() {
        LayoutInflater.from(context).inflate(R.layout.video_layout_player, this, true)
        goBack = video_back
        title = video_title
        moreFunc = video_more
        playStatus = video_play
        seekbar = video_seekbar
        current = video_current
        duration = video_duration
    }
}