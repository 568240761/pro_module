package com.ly.video.render

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
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
    @WorkerThread
    fun captureBitmap(callback: (bitmap: Bitmap) -> Unit)

    /**
     * GIF图
     *
     * @param path gif文件存储位置
     * @param handleCallback 函数类型处理回调
     * @param failureCallback 函数类型失败回调
     * @param successCallback 函数类型成功回调
     */
    @WorkerThread
    fun captureGif(
        path: String,
        handleCallback: () -> Unit = {},
        failureCallback: () -> Unit = {},
        successCallback: (file: File) -> Unit = {}
    )
}