package com.ly.video.suspension

import android.view.View
import android.widget.ImageView
import com.ly.video.R

/**
 * Created by LanYang on 2019/3/29
 *
 * 默认实现的有UI界面的悬浮窗口
 */
class DefaultSusWindow : AbstractSusWindow(), View.OnClickListener {

    private lateinit var mPlayStatus: ImageView

    override fun getLayoutId(): Int {
        return R.layout.video_layout_default_sus_window
    }

    override fun initView(view: View) {
        mPlayStatus = view.findViewById(R.id.iv_status)
        mPlayStatus.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_status -> {

            }
        }
    }
}