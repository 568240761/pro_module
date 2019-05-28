package com.ly.vidoetest.hover

import android.view.Gravity
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.getAllSuperClass
import com.ly.pub.util.retActionBarHeight
import com.ly.pub.util.retStatusHeight
import com.ly.vidoetest.player.VideoPlayerManager

/**
 * Created by LanYang on 2019/3/27
 * 悬浮窗口选项
 */
class HoverOption {

    private var mWidth = VideoPlayerManager.getIVideoPlayer().getWidth() / 2
    private var mHeight = VideoPlayerManager.getIVideoPlayer().getHeight() / 2
    private var mGravity: Int = Gravity.TOP or Gravity.START
    private var mXOffset: Int = retStatusHeight()
    private var mYOffset: Int = retActionBarHeight() + retStatusHeight()
    private var mCanMove = true
    /**其父类必须为[BaseHover]*/
    private var mClassType: Class<*> = LYHover::class.java

    /**
     * 悬浮窗口宽度
     * @param width 悬浮窗口宽度
     */
    fun setWidth(width: Int): HoverOption {
        mWidth = width
        return this
    }

    /**
     * 悬浮窗口高度
     * @param height 悬浮窗口高度
     */
    fun setHeight(height: Int): HoverOption {
        mHeight = height
        return this
    }

    /**
     * 悬浮窗口出现位置
     * @param gravity 值为[Gravity]中的TOP,BOTTOM,LEFT,RIGHT等
     */
    fun setGravity(gravity: Int): HoverOption {
        mGravity = gravity
        return this
    }

    /**
     * 悬浮窗口出现位置横坐标相对位移
     * @param xOffset 横坐标相对位移
     */
    fun setXOffset(xOffset: Int): HoverOption {
        mXOffset = xOffset
        return this
    }

    /**
     * 悬浮窗口出现位置纵坐标相对位移
     * @param yOffset 纵坐标相对位移
     */
    fun setYOffset(yOffset: Int): HoverOption {
        mYOffset = yOffset
        return this
    }

    /**
     * 悬浮窗口是否可以移动
     */
    fun setCanMove(canMove: Boolean): HoverOption {
        mCanMove = canMove
        return this
    }

    /**
     * 悬浮窗口界面
     * @param classType 悬浮窗口界面相关类
     */
    fun setClassType(classType: Class<*>): HoverOption {
        mClassType = classType
        return this
    }

    internal fun build(): BaseHover {
        val list = getAllSuperClass(mClassType)
        for (index in list.indices) {
            LogUtil_d(this@HoverOption.javaClass.simpleName, "superClass=${list[index].name}")
            if (list[index].name == BaseHover::class.java.name)
                break

            if (index + 1 == list.size)
                throw IllegalAccessException("mClassType必须为BaseHover的子类")
        }
        val player = mClassType.newInstance() as BaseHover
        player.setSize(mWidth, mHeight)
        player.setGravity(mGravity, mXOffset, mYOffset)
        player.setCanMove(mCanMove)
        return player
    }
}