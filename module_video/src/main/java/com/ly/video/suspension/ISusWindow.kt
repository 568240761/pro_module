package com.ly.video.suspension

/**
 * Created by LanYang on 2019/5/28
 *
 * 悬浮窗口接口
 */
interface ISusWindow {

    /**
     * 设置悬浮窗口大小;推荐宽高比与视频的宽高比相同
     *
     * @param width 窗口的宽
     * @param height 窗口的高
     */
    fun setSize(width: Int, height: Int)

    /**
     * 设置悬浮窗口出现的位置
     *
     * @param xOffset 横坐标相对位移
     * @param yOffset 纵坐标相对位移
     */
    fun setGravity(xOffset: Int, yOffset: Int)

    /**
     * 设置悬浮窗口是否可以移动
     *
     * @param canMove true,可以移动;false,不可以移动
     */
    fun setCanMove(canMove: Boolean)

    /**
     * 更新悬浮窗口位置
     *
     * @param x 横坐标相对位移
     * @param y 纵坐标相对位移
     */
    fun updatePosition(x: Int, y: Int)


    /**
     * 显示悬浮窗口
     */
    fun show()

    /**
     * 销毁悬浮窗口
     */
    fun dismiss()
}