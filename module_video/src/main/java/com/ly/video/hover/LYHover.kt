package com.ly.video.hover

import android.view.View
import android.widget.ImageView
import com.ly.video.R
import com.ly.video.player.IChangeUIListener
import com.ly.video.player.STATE_COMPLETED
import com.ly.video.player.STATE_PREPARED
import com.ly.video.player.VideoPlayerManager
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/3/29
 * 默认实现的有UI界面的悬浮窗口
 */
class LYHover : BaseHover(), View.OnClickListener {

    private lateinit var mPlayStatus: ImageView

    override fun getLayoutId(): Int {
        return R.layout.video_layout_hover_player
    }

    override fun initView(view: View) {
        mPlayStatus = view.findViewById(R.id.hover_status)
        mPlayStatus.setOnClickListener(this)
        if (VideoPlayerManager.getIVideoPlayer().getPlayStatus() == STATE_PREPARED ||
                VideoPlayerManager.getIVideoPlayer().getPlayStatus() == STATE_COMPLETED)
            mPlayStatus.visibility = View.VISIBLE

        VideoPlayerManager.getIVideoPlayer().clearListeners()
        VideoPlayerManager.getIVideoPlayer().setOnCompletionListener(IMediaPlayer.OnCompletionListener {
            if(mPlayStatus.visibility==View.GONE)
                mPlayStatus.visibility = View.VISIBLE
            mPlayStatus.setImageResource(R.drawable.video_layer_start)
        })
        VideoPlayerManager.getIVideoPlayer().setOnErrorListener()
        VideoPlayerManager.getIVideoPlayer().setIChangeUIListener(object : IChangeUIListener {
            override fun startCauseUI() {
                mPlayStatus.setImageResource(R.drawable.video_layer_pause)
            }

            override fun pauseCauseUI() {
                mPlayStatus.setImageResource(R.drawable.video_layer_start)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.hover_status -> {
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
}