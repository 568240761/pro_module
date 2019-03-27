package com.ly.video

/**
 * Created by LanYang on 2019/3/27
 * 悬浮播放管理;全局只允许一个悬浮播放器
 */
object HoverManager {

    /**是否存在悬浮播放器*/
    private var mIsHovering = false

    /**
     * 创建悬浮播放器
     */
    fun createHoverVideoPlayer() {
        if (mIsHovering) {
            destroyHoverVideoPlayer()
        }
    }

    /**
     * 销毁悬浮播放器
     */
    fun destroyHoverVideoPlayer() {

        mIsHovering = false
    }
}