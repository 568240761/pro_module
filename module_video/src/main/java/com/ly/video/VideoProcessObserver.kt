package com.ly.video

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d


/**
 * Created by LanYang on 2019/3/29
 * 对APP生命周期进行观察,在[VideoManager]中实例化
 */
class VideoProcessObserver : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "onPause")
    }
}