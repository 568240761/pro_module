package com.ly.video

import android.net.Uri

/**
 * Created by LanYang on 2019/2/27
 * 音频控制接口
 */
interface IVideoPlayer {

    /**
     * 初始化
     */
    fun init()

    /**释放资源*/
    fun release()

    /**
     * 设置视频URI
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    fun setVideoURI(uri: Uri, headers: Map<String, String>? = null, isDebug: Boolean)

    /**播放*/
    fun start()

    /**暂停*/
    fun pause()

    /**
     * 获取视频时长
     * @return 视频时长
     */
    fun getDuration(): Int

    /**
     * 获取视频当前播放进度
     * @return 当前播放进度值
     */
    fun getCurrentPosition(): Int

    /**
     * 进度跳转
     * @param pos 进度值
     */
    fun seekTo(pos: Long)

    /**
     * 是否正在播放
     * @return 如果正在播放,为true;否则,为false
     */
    fun isPlaying(): Boolean

    /**
     * 捕获视频中当前帧画面(截图)
     */
    fun captureFrame()

    /**
     * 捕获视频中多张帧画面(GIF)
     */
    fun captureFrames()
}