package com.ly.video.player

import androidx.lifecycle.ProcessLifecycleOwner
import com.ly.video.VideoProcessObserver

/**
 * Created by LanYang on 2019/3/28
 * 视频播放器管理
 */
object VideoPlayerManager {

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(VideoProcessObserver())
    }

    private var mPlayer: IVideoPlayer? = null

    fun createVideoPlayer(option: VideoPlayerOption) {
        if (isExistPlayer()) {
            destroyVideoPlayer()
        }
        mPlayer = option.build()
    }

    fun getIVideoPlayer(): IVideoPlayer {
        if (!isExistPlayer()) {
            throw IllegalAccessException("调用createVideoPlayer方法创建IVideoPlayer实例")
        }
        return mPlayer!!
    }

    fun destroyVideoPlayer() {
        if (isExistPlayer()) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer!!.destroy()
            mPlayer = null
        }
    }

    private fun isExistPlayer(): Boolean {
        return mPlayer != null
    }
}