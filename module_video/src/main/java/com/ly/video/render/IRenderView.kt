package com.ly.video.render

import android.graphics.Bitmap
import java.io.File


/**
 * Created by LanYang on 2019/3/1
 *
 * 关于渲染视频画面的接口
 */
interface IRenderView {

    /**
     * 根据视频宽度[videoWidth]和视频高度[videoHeight]来设置[IRenderView]实现类的宽高
     */
    fun setRenderViewSize(videoWidth: Int, videoHeight: Int)

    /**
     * 截图
     *
     * @param callback 函数类型回调
     */
    fun captureBitmap(callback: (bitmap: Bitmap) -> Unit)

    /**
     * GIF图
     *
     * @param path gif文件存储位置
     * @param failure 函数类型失败回调
     * @param success 函数类型成功回调
     */
    fun captureGif(
        path: String,
        failure: () -> Unit = {},
        success: (file: File) -> Unit = {}
    )
}