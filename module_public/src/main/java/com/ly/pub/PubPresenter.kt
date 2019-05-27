package com.ly.pub

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d

/**
 * Created by LanYang on 2018/8/6
 * 实现DefaultLifecycleObserver类,对Activity与Fragment生命周期进行观察;
 * 销毁时,会自动回调releaseResource()
 */
abstract class PubPresenter : DefaultLifecycleObserver {
    private lateinit var mLifecycleOwner: LifecycleOwner
    protected lateinit var activity: Activity

    open fun attachView(any: Any, paramsActivity: Activity? = null) {
        when {
            any is Activity -> activity = any
            any is Fragment -> any.activity?.let { activity = it }
            paramsActivity != null -> activity = paramsActivity
        }

        if (any is LifecycleOwner)
            mLifecycleOwner = any
        else
            paramsActivity?.let {
                if (paramsActivity is LifecycleOwner)
                    mLifecycleOwner = paramsActivity
            }

        if (this::mLifecycleOwner.isInitialized)
            mLifecycleOwner.lifecycle.addObserver(this)
    }

    /**
     * 当mView实例不为Activity或Fragment时,必要时可以手动调用该方法
     */
    fun detachView() {
        releaseResource()
    }

    /**
     * 释放资源；比如网络请求
     */
    open fun releaseResource() {
        PUBLIC_NETWORK_MANAGER.cancelTag(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil_d(this.javaClass.simpleName, "${this.javaClass.simpleName}-onDestroy")
        mLifecycleOwner.lifecycle.removeObserver(this)
        releaseResource()
    }
}