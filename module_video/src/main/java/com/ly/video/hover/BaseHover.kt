package com.ly.video.hover

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.video.R
import com.ly.video.render.IRenderView
import com.ly.video.render.SurfaceRenderView

/**
 * Created by LanYang on 2019/3/27
 * 悬浮窗口,默认有界面实现类[LYHover];
 * 如果需要自定义界面,必须继承[BaseHover];
 * 注意，布局资源文件中一定要包含id为hover_render的[IRenderView]控件
 */
abstract class BaseHover : IHover {

    private val mWindowManager = PUBLIC_APPLICATION.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val mLayoutParams = WindowManager.LayoutParams()

    private lateinit var mView: View

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
    }

    private fun setContentView() {
        val inflate = PUBLIC_APPLICATION.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflate.inflate(getLayoutId(), null)
        mRenderView = mView.findViewById<SurfaceRenderView>(R.id.hover_render)
        initView(mView)
    }

    abstract fun getLayoutId(): Int

    abstract fun initView(view: View)

    override fun setSize(width: Int, height: Int) {
        mLayoutParams.width = width
        mLayoutParams.height = height
        mRenderView.setVideoSize(width, height)
    }

    override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        mLayoutParams.gravity = gravity
        mLayoutParams.x = xOffset
        mLayoutParams.y = yOffset
    }

    override fun setCanMove(canMove: Boolean) {
        if (canMove) {
            mView.setOnTouchListener(object : View.OnTouchListener {
                private var lastX = 0f
                private var lastY = 0f
                private var changeX = 0f
                private var changeY = 0f

                @SuppressLint("ClickableViewAccessibility")
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = event.rawX
                            lastY = event.rawY
                            LogUtil_d(this@BaseHover.javaClass.simpleName, "ACTION_DOWN[lastX=$lastX lastY=$lastY]")
                        }
                        MotionEvent.ACTION_MOVE -> {
                            changeX = event.rawX - lastX
                            changeY = event.rawY - lastY
                            updatePosition(changeX.toInt(), changeY.toInt())
                            lastX = event.rawX
                            lastY = event.rawY
                            LogUtil_d(this@BaseHover.javaClass.simpleName, "ACTION_MOVE[lastX=$lastX lastY=$lastY]")
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
        LogUtil_d(this@BaseHover.javaClass.simpleName, "updatePosition[x=${mLayoutParams.x} y=${mLayoutParams.y}]")
        mWindowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun show() {
        mWindowManager.addView(mView, mLayoutParams)
    }

    override fun dismiss() {
        mWindowManager.removeViewImmediate(mView)
    }
}