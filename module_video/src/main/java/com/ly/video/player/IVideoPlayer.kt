package com.ly.video.player

import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.ly.video.render.IRenderView
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/2/27
 * 音频控制接口
 */
interface IVideoPlayer {

    /**
     * 设置开发状态
     * @param isDebug 是否为开发状态;为true,显示播放log日志
     */
    fun setDebug(isDebug: Boolean)

    /**
     * 设置视频URI
     * @param uri 视频URI
     * @param headers 与数据请求一起发送的标头(网络视频)
     *                请注意，默认情况下允许跨域重定向，但可以通过headers参数更改键/值对，
     *                “android-allow-cross-domain-redirect”作为键，“0”或“1”作为禁止或允许跨域重定向的值。
     *
     */
    fun changeVideoURI(uri: Uri, headers: Map<String, String>? = null)

    /**
     * 绑定显示画面控件
     * @param render 画面控件
     */
    fun bindRender(render: IRenderView)

    /**
     * 设置显示画面的[SurfaceHolder]
     */
    fun setDisplay(holder: SurfaceHolder?)

    /**
     * 设置显示画面的[Surface]
     */
    fun setSurface(surface: Surface?)

    /**视频准备好后,回调OnPreparedListener接口中的 onPrepared方法*/
    fun prepareAsync()

    /**视频宽度*/
    fun getWidth(): Int

    /**视频高度*/
    fun getHeight(): Int

    /**播放*/
    fun start()

    /**暂停*/
    fun pause()

    /**停止*/
    fun stop()

    /**释放资源*/
    fun release()

    /**开启后台播放*/
    fun canBackground()

    /**关闭后台播放*/
    fun stopBackground()

    /**
     * 获取视频时长
     * @return 视频时长
     */
    fun getDuration(): Long

    /**
     * 获取视频当前播放进度
     * @return 当前播放进度值
     */
    fun getCurrentPosition(): Long

    /**
     * 进度跳转
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
     * @return 返回播放状态
     */
    fun getPlayStatus(): Int

    /**
     * 是否可以截图
     * @return true,能截图;false,不能
     */
    fun isCanCapture(): Boolean

    /**
     * 捕获视频中当前帧画面(截图)
     */
    fun captureFrame()

    /**
     * 捕获视频中多张帧画面(GIF)
     */
    fun captureFrames()

    /**销毁*/
    fun destroy()

    fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener? = null)

    fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener? = null)

    fun setOnBufferingUpdateListener(listener: IMediaPlayer.OnBufferingUpdateListener)

    fun setOnSeekCompleteListener(listener: IMediaPlayer.OnSeekCompleteListener)

    fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener)

    fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener? = null)

    fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener)

    fun setOnTimedTextListener(listener: IMediaPlayer.OnTimedTextListener)

    fun setIChangeUIListener(listener: IChangeUIListener?)

    fun clearListeners()
}

interface IChangeUIListener {
    /**
     * 视频播放回调
     */
    fun startCauseUI()

    /**
     * 视频暂停回调
     */
    fun pauseCauseUI()
}