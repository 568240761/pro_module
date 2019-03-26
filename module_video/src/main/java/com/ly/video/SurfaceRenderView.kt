package com.ly.video

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.ly.pub.util.LogUtil_d

/**
 * Created by LanYang on 2019/3/4
 */
class SurfaceRenderView : SurfaceView, IRenderView {

    private var mMeasureHelper: MeasureHelper? = null

    private val mSurfaceViewCallback = SurfaceViewCallback()

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
        initView()
    }

    private fun initView() {
        val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        )
        layoutParams = lp

        mMeasureHelper = MeasureHelper(this)
        holder.addCallback(mSurfaceViewCallback)
        LogUtil_d(this.javaClass.simpleName, "initView完成")
    }

    override fun getView(): View {
        return this
    }

    override fun resetLayout() {
        LogUtil_d(this.javaClass.simpleName, "requestLayout开始")
        requestLayout()
    }

    override fun bindMediaPlayer(player: IVideoPlayer) {
        mSurfaceViewCallback.setPlayer(player)
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mMeasureHelper!!.setVideoSize(videoWidth, videoHeight)
        holder.setFixedSize(videoWidth, videoHeight)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        LogUtil_d(this.javaClass.simpleName, "onMeasure开始")
        mMeasureHelper!!.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper!!.getMeasuredWidth(), mMeasureHelper!!.getMeasuredHeight())
    }
}

class SurfaceViewCallback : SurfaceHolder.Callback {
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mPlayer: IVideoPlayer? = null

    fun setPlayer(player: IVideoPlayer) {
        LogUtil_d(this@SurfaceViewCallback.javaClass.simpleName, "setPlayer")
        mPlayer = player
        if (mSurfaceHolder != null) mPlayer!!.bindSurfaceHolder(mSurfaceHolder)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        LogUtil_d(
                this@SurfaceViewCallback.javaClass.simpleName,
                "surfaceChanged[format=$format;width=$width;height=$height]"
        )
    }


    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        LogUtil_d(this@SurfaceViewCallback.javaClass.simpleName, "surfaceDestroyed")
        mSurfaceHolder = null
        mPlayer?.bindSurfaceHolder(mSurfaceHolder)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        LogUtil_d(this@SurfaceViewCallback.javaClass.simpleName, "surfaceCreated")
        mSurfaceHolder = holder
        mPlayer?.bindSurfaceHolder(mSurfaceHolder)
    }

}