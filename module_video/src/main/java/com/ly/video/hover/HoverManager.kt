package com.ly.video.hover

/**
 * Created by LanYang on 2019/3/27
 * 悬浮窗口管理;全局只允许一个悬浮窗口
 */
object HoverManager {

    private var mHoverPlayer: IHover? = null

    /**
     * 创建悬浮窗口
     */
    fun createHoverPlayer(option: HoverOption) {
        if (isExistHover()) {
            destroyHoverPlayer()
        }
        mHoverPlayer = option.build()
    }

    /**显示悬浮窗口*/
    fun show() {
        if (isExistHover()) {
            mHoverPlayer!!.show()
        }
    }

    /**
     * 销毁悬浮窗口
     */
    fun destroyHoverPlayer() {
        if (isExistHover()) {
            mHoverPlayer!!.dismiss()
            mHoverPlayer = null
        }
    }

    private fun isExistHover(): Boolean {
        return mHoverPlayer != null
    }
}