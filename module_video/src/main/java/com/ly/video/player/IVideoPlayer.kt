package com.ly.video.player

import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.ly.video.annotation.VideoStatus

/**
 * Created by LanYang on 2019/5/28
 *
 * 关于音频控制接口
 */
interface IVideoPlayer {

    /**
     * 设置开发状态
     *
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    fun setDebug(isDebug: Boolean = false)

    /**
     * 设置视频URI
     *
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     *
     */
    fun setVideoURI(uri: Uri, headers: Map<String, String>? = null)

    /**
     * 设置显示画面的[SurfaceHolder]
     */
    fun setDisplay(holder: SurfaceHolder?)

    /**
     * 设置显示画面的[Surface]
     */
    fun setSurface(surface: Surface?)

    /**
     * 视频准备好后,会调用函数类型参数[prepared]
     *
     * @param prepared 视频准备好时,会调用的函数类型参数
     * @param completion 视频播放完成时,会调用的函数类型参数
     * @param error 视频播放出现错误时,会调用的函数类型参数
     */
    fun prepareAsync(
        prepared: (width: Int, height: Int) -> Unit,
        completion: () -> Unit = {},
        error: (msg: String) -> Unit = {}
    )

    /**视频宽度*/
    fun getWidth(): Int

    /**视频高度*/
    fun getHeight(): Int

    /**
     * 播放
     *
     */
    fun start()

    /**
     * 暂停
     *
     */
    fun pause()

    /**
     * 停止
     *
     */
    fun stop()

    /**
     * 释放资源
     *
     */
    fun release()

    /**
     * 销毁
     *
     */
    fun destroy()

    /**
     * 获取视频时长
     *
     * @return 视频时长
     */
    fun getDuration(): Long

    /**
     * 获取视频当前播放进度
     *
     * @return 当前播放进度值
     */
    fun getCurrentPosition(): Long

    /**
     * 进度跳转[pos]
     *
     * @param pos 进度值
     */
    fun seekTo(pos: Long)

    /**
     * 是否处于就绪状态
     */
    fun isPreparedState(): Boolean

    /**
     * 是否正在播放
     * @return 如果正在播放,为true;否则,为false
     */
    fun isPlaying(): Boolean

    /**
     * 视频播放状态
     *
     * @return 返回播放状态
     */
    @VideoStatus
    fun getPlayStatus(): Int

    fun setUIOperatorListener(listener: IUIOperatorListener?)

    fun setOnPreparedListener(listener: (width: Int, height: Int) -> Unit)

    fun setOnCompletionListener(listener: () -> Unit = {})

    fun setOnBufferingUpdateListener(listener: (percent: Int) -> Unit)

    fun setOnSeekCompleteListener(listener: () -> Unit)

    fun setOnVideoSizeChangedListener(listener: (width: Int, height: Int, sar_num: Int, sar_den: Int) -> Unit)

    fun setOnErrorListener(listener: (msg: String) -> Unit = {})

    fun setOnInfoListener(listener: (what: Int, extra: Int) -> Unit)

    fun setOnTimedTextListener(listener: (text: TimedText) -> Unit)

    fun clearAllListener()

    /**
     * 响应视频播放开始或停止,UI变化的回调接口
     */
    interface IUIOperatorListener {
        fun start()

        fun pause()

        fun stop()

        fun release()

        fun destroy()
    }
}