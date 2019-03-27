package com.ly.video

import com.ly.pub.util.retScreenHeightDp
import com.ly.pub.util.retScreenWidthDp

/**
 * Created by LanYang on 2019/3/27
 * 悬浮选项
 */
class HoverOption {

    private var mWidth = retScreenWidthDp() / 2
    private var mHeight = retScreenHeightDp() / 3

    fun setWidth(width: Int): HoverOption {
        mWidth = width
        return this
    }

    fun setHeight(height: Int): HoverOption {
        mHeight = height
        return this
    }

    fun build(): BaseHoverPlayer {
        val player = BaseHoverPlayer()
        return player
    }
}