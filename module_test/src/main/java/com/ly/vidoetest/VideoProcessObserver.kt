package com.ly.vidoetest

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_e
import com.ly.vidoetest.player.VideoPlayerManager


/**
 * Created by LanYang on 2019/3/29
 * 对APP生命周期进行观察,在[VideoPlayerManager]中实例化
 */
class VideoProcessObserver : DefaultLifecycleObserver {

    private var mSystemPause = false

    override fun onResume(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onResume")

        try {
            if (mSystemPause && VideoPlayerManager.getIVideoPlayer().isPreparedState()) {
                VideoPlayerManager.getIVideoPlayer().start()
                mSystemPause = false
            }
        } catch (e: Exception) {//[IVideoPlayer]从未实例化,则会抛异常
            LogUtil_e(e)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onPause")

        try {
            if (!mSystemPause &&
                VideoPlayerManager.getIVideoPlayer().isPreparedState() &&
                VideoPlayerManager.getIVideoPlayer().isPlaying()
            ) {
                VideoPlayerManager.getIVideoPlayer().pause()
                mSystemPause = true
            }
        } catch (e: Exception) {//[IVideoPlayer]从未实例化,则会抛异常
            LogUtil_e(e)
        }
    }
}