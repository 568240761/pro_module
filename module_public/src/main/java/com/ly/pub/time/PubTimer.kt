package com.ly.pub.time

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d
import java.util.*

/**
 * Created by LanYang on 2019/5/18
 * 关于执行周期性任务的类
 */
class PubTimer(val context: Context? = null) : DefaultLifecycleObserver {
    private val mTimer = Timer()

    init {
        context?.let {
            if (it is LifecycleOwner) it.lifecycle.addObserver(this)
        }
    }

    /**
     * 开始执行周期性任务
     *
     * @param delay 延时毫秒
     * @param time 间隔毫秒
     * @param timerTask 任务
     */
    fun start(delay: Long = 0L, time: Long, timerTask: TimerTask) {
        mTimer.schedule(timerTask, delay, time)
    }

    /**
     * 停止执行周期性任务
     *
     * 如果[context]是[LifecycleOwner]的子类,无需手动调用该方法;
     * 当[context]生命周期结束时,会自动调用该方法
     */
    fun stop() {
        mTimer.cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "${this.javaClass.simpleName}-onDestroy")

        (context as LifecycleOwner).lifecycle.removeObserver(this)
        stop()
    }
}