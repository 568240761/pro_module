package com.ly.video.player

import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_i
import com.ly.video.render.IRenderView
import com.ly.video.service.VideoPlayerService
import tv.danmaku.ijk.media.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Created by LanYang on 2019/2/27
 * 视频播放器
 */

const val STATE_ERROR = -1
const val STATE_IDLE = STATE_ERROR + 1
const val STATE_PREPARING = STATE_IDLE + 1
const val STATE_PREPARED = STATE_PREPARING + 1
const val STATE_PLAYING = STATE_PREPARED + 1
const val STATE_PAUSED = STATE_PLAYING + 1
const val STATE_COMPLETED = STATE_PAUSED + 1
const val STATE_STOP = STATE_COMPLETED + 1

class BaseVideoPlayer : IVideoPlayer {

    private var mCurrentState = STATE_IDLE

    private lateinit var mRenderView: IRenderView

    private val mMediaPlayer: IMediaPlayer = IjkMediaPlayer()

    private var mChangeUIListener: IChangeUIListener? = null

    init {
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        if (mMediaPlayer is IjkMediaPlayer) {
            /*1-硬解码,0-软件解码*/
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)

            //调用seekTo时,使用关键帧
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)

            //1-视频缓冲完成后,就开始播放；0-视频缓冲完成后,先暂停
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer.setScreenOnWhilePlaying(true)
    }

    override fun setDebug(isDebug: Boolean) {
        if (isDebug) IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)
    }

    override fun changeVideoURI(uri: Uri, headers: Map<String, String>?) {
        LogUtil_d(this.javaClass.simpleName, "Uri=$uri Header=${headers?.toString()}")
        mMediaPlayer.setDataSource(PUBLIC_APPLICATION, uri, headers)
    }

    override fun bindRender(render: IRenderView) {
        mRenderView = render
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        mMediaPlayer.setDisplay(holder)
    }

    override fun setSurface(surface: Surface?) {
        mMediaPlayer.setSurface(surface)
    }

    override fun prepareAsync() {
        mMediaPlayer.prepareAsync()
        mCurrentState = STATE_PREPARING
    }

    override fun getWidth(): Int {
        return if (isPreparedState()) mMediaPlayer.videoWidth else 0
    }

    override fun getHeight(): Int {
        return if (isPreparedState()) mMediaPlayer.videoHeight else 0
    }

    override fun start() {
        if (isPreparedState()) {
            LogUtil_d(this.javaClass.simpleName, "start")
            mMediaPlayer.start()
            mChangeUIListener?.startCauseUI()
            mCurrentState = STATE_PLAYING
        }
    }

    override fun pause() {
        if (isPlaying()) {
            LogUtil_d(this.javaClass.simpleName, "pause")
            mMediaPlayer.pause()
            mChangeUIListener?.pauseCauseUI()
            mCurrentState = STATE_PAUSED
        }
    }

    override fun stop() {
        LogUtil_d(this.javaClass.simpleName, "stop")
        mMediaPlayer.stop()
        mCurrentState = STATE_STOP
    }

    override fun release() {
        LogUtil_d(this.javaClass.simpleName, "release")
        mMediaPlayer.release()
        mCurrentState = STATE_IDLE
    }

    override fun canBackground() {
        VideoPlayerService.mediaPlayer = mMediaPlayer
        VideoPlayerService.intentToStart(PUBLIC_APPLICATION)
    }

    override fun stopBackground() {
        VideoPlayerService.mediaPlayer = null
        VideoPlayerService.intentToStop(PUBLIC_APPLICATION)
    }

    override fun getDuration(): Long {
        return if (isPreparedState()) {
            mMediaPlayer.duration
        } else 0L
    }

    override fun getCurrentPosition(): Long {
        return if (isPreparedState()) {
            mMediaPlayer.currentPosition
        } else 0L
    }

    override fun seekTo(pos: Long) {
        if (isPreparedState()) mMediaPlayer.seekTo(pos)
    }

    override fun isPreparedState(): Boolean {
        return mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING
    }

    override fun isPlaying(): Boolean {
        return isPreparedState() && mMediaPlayer.isPlaying
    }

    override fun getPlayStatus(): Int {
        return mCurrentState
    }

    override fun isCanCapture(): Boolean {
        return this::mRenderView.isInitialized && mRenderView is TextureView
    }

    override fun captureFrame(capture: ICaptureFrame) {
        if (isCanCapture()) {
            mRenderView.captureFrame(capture)
        }
    }

    override fun captureFrames() {
        if (isCanCapture()) {

        }
    }

    override fun destroy() {
        LogUtil_d(this.javaClass.simpleName, "destroy")
        IjkMediaPlayer.native_profileEnd()
    }

    override fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener?) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnPreparedListener")

        val preparedListener = IMediaPlayer.OnPreparedListener {
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "IMediaPlayer.OnPreparedListener")

            mCurrentState = STATE_PREPARED

            val videoWidth = it.videoWidth
            val videoHeight = it.videoHeight
            LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$videoWidth")
            LogUtil_d(this.javaClass.simpleName, "mVideoHeight=$videoHeight")

            if (videoWidth > 0 && videoHeight > 0) {
                mRenderView.setVideoSize(videoWidth, videoHeight)
            }

            listener?.onPrepared(it)
        }
        mMediaPlayer.setOnPreparedListener(preparedListener)
    }

    override fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener?) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnCompletionListener")
        val completionListener = IMediaPlayer.OnCompletionListener { mp: IMediaPlayer ->
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "OnCompletionListener")
            mCurrentState = STATE_COMPLETED
            listener?.onCompletion(mp)
        }
        mMediaPlayer.setOnCompletionListener(completionListener)
    }

    override fun setOnBufferingUpdateListener(listener: IMediaPlayer.OnBufferingUpdateListener) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnBufferingUpdateListener")
        mMediaPlayer.setOnBufferingUpdateListener(listener)
    }

    override fun setOnSeekCompleteListener(listener: IMediaPlayer.OnSeekCompleteListener) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnSeekCompleteListener")
        mMediaPlayer.setOnSeekCompleteListener(listener)
    }

    override fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnVideoSizeChangedListener")
        mMediaPlayer.setOnVideoSizeChangedListener(listener)
    }

    override fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener?) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnErrorListener")
        val errorListener = IMediaPlayer.OnErrorListener { mp: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "OnErrorListener[what=$what,extra=$extra]")

            mCurrentState = STATE_ERROR
            listener?.onError(mp, what, extra)
            true
        }
        mMediaPlayer.setOnErrorListener(errorListener)
    }

    override fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener) {
        LogUtil_i(this@BaseVideoPlayer.javaClass.simpleName, "setOnInfoListener")
        mMediaPlayer.setOnInfoListener(listener)
    }

    override fun setOnTimedTextListener(listener: IMediaPlayer.OnTimedTextListener) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnTimedTextListener")
        mMediaPlayer.setOnTimedTextListener(listener)
    }

    override fun setIChangeUIListener(listener: IChangeUIListener?) {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "setOnTimedTextListener")
        mChangeUIListener = listener
    }

    override fun clearListeners() {
        LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "clearListeners")
        if (mMediaPlayer is AbstractMediaPlayer) {
            mMediaPlayer.resetListeners()
        }
        mChangeUIListener = null
    }
}
