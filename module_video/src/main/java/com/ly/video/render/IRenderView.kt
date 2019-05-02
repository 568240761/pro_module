package com.ly.video.render

import android.graphics.Bitmap


/**
 * Created by LanYang on 2019/3/1
 * 关于渲染画面的接口
 */
interface IRenderView {

    fun setVideoSize(videoWidth: Int, videoHeight: Int)

    fun captureFrame(){}
}