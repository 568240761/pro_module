package com.ly.video.suspension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.retScreenWidthPx
import com.ly.video.VideoManager
import com.ly.video.render.IRenderView
import com.ly.video.render.SurfaceRenderView

/**显示悬浮窗口*/
fun showSusWindow() {
    val susWindow = VideoManager.getSusWindowUI().newInstance() as AbstractSusWindow
    susWindow.setGravity(VideoManager.getSusWindowX(), VideoManager.getSusWindowY())
    susWindow.setCanMove(VideoManager.isSusWindowMove())
    susWindow.show()
}

/**
 * Created by LanYang on 2019/3/27
 *
 * 悬浮窗口基类;
 * 默认会使用有UI界面的悬浮窗口[DefaultSusWindow],可以通过继承[AbstractSusWindow]类来实现自定义UI界面的悬浮窗口
 */
abstract class AbstractSusWindow : ISusWindow {

    private val mWindowManager = PUBLIC_APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val mLayoutParams = WindowManager.LayoutParams()

    private val mView = FrameLayout(PUBLIC_APPLICATION)

    private lateinit var mRenderView: IRenderView

    init {
        setContentView()

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        mLayoutParams.type = type
        mLayoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams.windowAnimations = 0
        mLayoutParams.format = PixelFormat.RGBA_8888

        setSize()
    }

    private fun setContentView() {
        mView.layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        val surfaceRenderView = SurfaceRenderView(PUBLIC_APPLICATION)
        surfaceRenderView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        mView.addView(surfaceRenderView)
        mRenderView = surfaceRenderView

        val inflate = PUBLIC_APPLICATION.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflate.inflate(getLayoutId(), mView, true)
        initView(mView)
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView(view: View)

    private fun setSize() {
        mLayoutParams.width = retScreenWidthPx() / VideoManager.getSusWindowWidthFactor()

        val factor = VideoManager.videoPlayer.getWidth().toFloat() / VideoManager.videoPlayer.getHeight()
        mLayoutParams.height = (mLayoutParams.width / factor).toInt()

        mRenderView.setRenderViewSize(mLayoutParams.width, mLayoutParams.height)
    }

    override fun setGravity(xOffset: Int, yOffset: Int) {
        mLayoutParams.gravity = Gravity.TOP or Gravity.START
        mLayoutParams.x = xOffset
        mLayoutParams.y = yOffset
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setCanMove(canMove: Boolean) {
        if (canMove) {
            mView.setOnTouchListener(object : View.OnTouchListener {
                private var lastX = 0f
                private var lastY = 0f
                private var changeX = 0f
                private var changeY = 0f

                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX
                            lastY = event.rawY
                            LogUtil_d(this.javaClass.simpleName, "ACTION_DOWN[lastX=$lastX lastY=$lastY]")
                        }
                        MotionEvent.ACTION_MOVE -> {
                            changeX = event.rawX - lastX
                            changeY = event.rawY - lastY
                            updatePosition(changeX.toInt(), changeY.toInt())
                            lastX = event.rawX
                            lastY = event.rawY
                            LogUtil_d(this.javaClass.simpleName, "ACTION_MOVE[lastX=$lastX lastY=$lastY]")
                        }
                    }
                    return false
                }
            })
        }
    }

    override fun updatePosition(x: Int, y: Int) {
        mLayoutParams.x += x
        mLayoutParams.y += y
        LogUtil_d(this.javaClass.simpleName, "updatePosition[x=${mLayoutParams.x} y=${mLayoutParams.y}]")
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun show() {
        mWindowManager.addView(mView, mLayoutParams)
    }

    override fun dismiss() {
        mWindowManager.removeViewImmediate(mView)
    }
}