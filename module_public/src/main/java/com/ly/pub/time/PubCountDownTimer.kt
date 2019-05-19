package com.ly.pub.time

import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d

/**
 * Created by LanYang on 2019/5/18
 * 关于执行倒计时任务的类
 */
class PubCountDownTimer(
    val totalTime: Long,
    val countDownInterval: Long,
    val context: Context? = null
) : DefaultLifecycleObserver {

    private lateinit var mCountDownTimer: CountDownTimer

    init {
        context?.let {
            if (it is LifecycleOwner) it.lifecycle.addObserver(this)
        }
    }

    /**
     * 开始执行倒计时任务
     *
     * @param tick 每[countDownInterval]s,开始执行任务
     * @param finish 倒计时任务完成
     */
    fun start(tick: (millisUntilFinished: Long) -> Unit, finish: () -> Unit) {
        mCountDownTimer = object : CountDownTimer(totalTime, countDownInterval) {
            override fun onFinish() {
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {
                tick(millisUntilFinished)
            }

        }
        mCountDownTimer.start()
    }

    /**
     * 停止执行倒计时任务
     *
     * 如果[context]是[LifecycleOwner]的子类,无需手动调用该方法;
     * 当[context]生命周期结束时,会自动调用该方法
     */
    fun stop() {
        mCountDownTimer.cancel()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "${this.javaClass.simpleName}-onDestroy")

        (context as LifecycleOwner).lifecycle.removeObserver(this)
        stop()
    }
}