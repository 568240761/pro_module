package com.ly.pub

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.pub.util.LogUtil_d

/**
 * Created by LanYang on 2018/8/6
 * MVP模式中的PRESENTER类都需要继承该类
 * 实现DefaultLifecycleObserver类,对Activity与Fragment生命周期进行观察;
 * 销毁时,会自动回调releaseResource()
 */
abstract class PubPresenter<T> : DefaultLifecycleObserver {
    private var mView: T? = null
    protected var activity: Activity? = null

    fun attachView(view: T, paramsActivity: Activity? = null) {
        if (view !is PubView)
            throw IllegalArgumentException("泛型的父类必须为PubView！！！")
        mView = view
        when {
            mView is Activity -> activity = mView as Activity
            mView is Fragment -> activity = (mView as Fragment).activity as Activity
            paramsActivity != null -> activity = paramsActivity
        }
        if (mView is LifecycleOwner) {
            (mView as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    /**
     * 当mView实例不为Activity或Fragment时,需要手动调用该方法
     */
    fun detachView() {
        releaseResource()
        mView = null
    }

    private fun isViewAttached(): Boolean {
        return mView != null
    }

    @Suppress("UNCHECKED_CAST")
    fun getMvpView(): T {
        if (!isViewAttached())
            throw IllegalArgumentException("请先调用attachView方法！！！")
        return mView as T
    }

    /**
     * 释放资源；比如网络请求
     */
    open fun releaseResource() {
        PUBLIC_NETWORK_MANAGER.cancelTag(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        (mView as LifecycleOwner).lifecycle.removeObserver(this)
        LogUtil_d(this.javaClass.simpleName, "${this.javaClass.simpleName}-onDestroy")
        releaseResource()
    }
}