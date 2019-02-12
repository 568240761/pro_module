package com.ly.pub

import android.content.Context
import androidx.fragment.app.Fragment
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.Receiver
import com.ly.pub.util.registerReceivers
import com.ly.pub.util.removeReceivers

/**
 * Created by LanYang on 2018/8/10
 */
abstract class PubFragment : Fragment(), Receiver {
    protected lateinit var activity: PubActivity
    /**是否为第一次可见*/
    var isFirstVisible = true

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as PubActivity
        registerReceivers(this)
    }

    /**
     * ViewPager与Fragment联合使用的时候有效
     * @param isVisibleToUser 如果该Fragment的UI对用户可见(默认)，则为true;如果不是，则为false。
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtil_d("PubFragment","${this::class.java.simpleName}-isVisibleToUser:"+isVisibleToUser)
        if (isVisibleToUser) {
            change()
            if (isFirstVisible) {
                lazyLoad()
                isFirstVisible = false
            }
        }
    }

    /**
     * 当多个Fragment一起使用时，进入不同Fragment时；
     * 如果有不同的地方时，就可以重写该方法来实现。
     * 比如：状态栏的设置
     */
    open fun change() {}

    /**
     * 实现懒加载的fragment页面，重写该方法
     */
    open fun lazyLoad() {}

    override fun onDetach() {
        super.onDetach()
        removeReceivers(this)
    }
}