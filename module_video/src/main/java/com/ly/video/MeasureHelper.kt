package com.ly.video

import android.view.View
import com.ly.pub.util.LogUtil_d
import java.lang.ref.WeakReference

/**
 * Created by LanYang on 2019/3/4
 */
class MeasureHelper(view: View) {

    private var mWeakView: WeakReference<View>? = null

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0

    private var mMeasuredWidth: Int = 0
    private var mMeasuredHeight: Int = 0

    init {
        mWeakView = WeakReference(view)
    }

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoWidth = videoWidth
        mVideoHeight = videoHeight
    }


    fun getMeasuredWidth(): Int {
        return mMeasuredWidth
    }

    fun getMeasuredHeight(): Int {
        return mMeasuredHeight
    }

    fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var width = View.getDefaultSize(mVideoWidth, widthMeasureSpec)
        var height = View.getDefaultSize(mVideoHeight, heightMeasureSpec)
        LogUtil_d(this.javaClass.simpleName, "Width=$width-Height=$height")
        LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$mVideoWidth-mVideoHeight=$mVideoHeight")

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            val factor = mVideoWidth.toFloat() / mVideoHeight
            val tempHeight = (width.toFloat() / factor).toInt()
            if (tempHeight > height) {
                val tempWidth = (height * factor).toInt()
                if (tempWidth <= width) {
                    width = tempWidth
                    LogUtil_d(this.javaClass.simpleName, "以高为基准[tempWidth=$tempWidth-Height=$height]")
                }
            } else {
                height = tempHeight
                LogUtil_d(this.javaClass.simpleName, "以宽为基准[Width=$width-tempHeight=$tempHeight]")
            }
        } else {
            LogUtil_d(this.javaClass.simpleName, "未获取视频真实宽高")
        }

        mMeasuredWidth = width
        mMeasuredHeight = height
    }
}