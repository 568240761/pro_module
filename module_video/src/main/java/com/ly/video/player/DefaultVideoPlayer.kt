package com.ly.video.player

import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.video.annotation.*
import tv.danmaku.ijk.media.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkTimedText

/**
 * 是否已经加载了"libijkplayer.so",默认为false
 */
private var mIsLoadedSoLib = false

/**
 * Created by LanYang on 2019/5/28
 *
 * 基于开源项目(https://github.com/bilibili/ijkplayer)实现的视频播放器
 */
internal class DefaultVideoPlayer : IVideoPlayer {

    @VideoStatus
    private var mCurrentState = STATE_IDLE

    private var mIsInitPlayer = false

    private lateinit var mMediaPlayer: IMediaPlayer

    private var mOnlyOperator: IVideoPlayer.IUIOperatorListener? = null

    private var mAllOperator: IVideoPlayer.IUIOperatorListener? = null

    init {
        initPlayer()
    }

    private fun initPlayer() {
        if (!mIsInitPlayer) {
            LogUtil_d(this.javaClass.simpleName, "initPlayer")

            mMediaPlayer = IjkMediaPlayer()
            mIsInitPlayer = true

            if (!mIsLoadedSoLib) {
                IjkMediaPlayer.native_profileBegin("libijkplayer.so")
                mIsLoadedSoLib = true
            }

            if (mMediaPlayer is IjkMediaPlayer) {
                //1-硬解码,0-软件解码
                (mMediaPlayer as IjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
                //自动旋转视频角度
                (mMediaPlayer as IjkMediaPlayer).setOption(
                    IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "mediacodec-auto-rotate",
                    1
                )
                (mMediaPlayer as IjkMediaPlayer).setOption(
                    IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "mediacodec-handle-resolution-change",
                    1
                )

                //调用seekTo时,使用关键帧
                (mMediaPlayer as IjkMediaPlayer).setOption(
                    IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "enable-accurate-seek",
                    1
                )

                //1-视频缓冲完成后,就开始播放；0-视频缓冲完成后,先暂停
                (mMediaPlayer as IjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1)
            }

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.setScreenOnWhilePlaying(true)
        }
    }

    override fun setDebug(isDebug: Boolean) {
        if (isDebug) IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)
    }

    override fun setVideoURI(uri: Uri, headers: Map<String, String>?) {
        LogUtil_d(this.javaClass.simpleName, "Uri=$uri Header=${headers?.toString()}")

        initPlayer()
        mMediaPlayer.setDataSource(PUBLIC_APPLICATION, uri, headers)
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        mMediaPlayer.setDisplay(holder)
    }

    override fun setSurface(surface: Surface?) {
        mMediaPlayer.setSurface(surface)
    }

    override fun prepareAsync(
        prepared: (width: Int, height: Int) -> Unit
    ) {
        mMediaPlayer.prepareAsync()
        mCurrentState = STATE_PREPARING

        setOnPreparedListener(prepared)
    }

    override fun getWidth(): Int {
        return if (isPreparedState()) mMediaPlayer.videoWidth else 0
    }

    override fun getHeight(): Int {
        return if (isPreparedState()) mMediaPlayer.videoHeight else 0
    }

    override fun start(callback: () -> Unit) {
        if (isPreparedState()) {
            LogUtil_d(this.javaClass.simpleName, "start")

            mMediaPlayer.start()
            mCurrentState = STATE_PLAYING

            callback()
            mOnlyOperator?.start()
            mAllOperator?.start()
        }
    }

    override fun pause(callback: () -> Unit) {
        if (isPlaying()) {
            LogUtil_d(this.javaClass.simpleName, "pause")

            mMediaPlayer.pause()
            mCurrentState = STATE_PAUSED

            callback()
            mOnlyOperator?.pause()
            mAllOperator?.pause()
        }
    }

    override fun stop(callback: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "stop")

        mMediaPlayer.stop()
        mCurrentState = STATE_STOP

        callback()
        mOnlyOperator?.stop()
        mAllOperator?.stop()
    }

    override fun reset(callback: () -> Unit) {
        mMediaPlayer.reset()
        mCurrentState = STATE_IDLE

        callback()
        mOnlyOperator?.reset()
        mAllOperator?.reset()
    }

    override fun release(callback: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "release")

        mMediaPlayer.release()
        mCurrentState = STATE_IDLE

        callback()
        mOnlyOperator?.release()
        mAllOperator?.release()

        mIsInitPlayer = false
    }

    override fun destroy() {
        LogUtil_d(this.javaClass.simpleName, "destroy")

        IjkMediaPlayer.native_profileEnd()
        mIsLoadedSoLib = false
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

    override fun setOnlyOperatorListener(listener: IVideoPlayer.IUIOperatorListener?) {
        mOnlyOperator = listener
    }

    override fun setAllOperatorListener(listener: IVideoPlayer.IUIOperatorListener?) {
        mAllOperator = listener
    }

    override fun setOnPreparedListener(listener: (width: Int, height: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnPreparedListener")

        initPlayer()
        mMediaPlayer.setOnPreparedListener {
            LogUtil_d(this.javaClass.simpleName, "IMediaPlayer.OnPreparedListener")

            mCurrentState = STATE_PREPARED

            val videoWidth = it.videoWidth
            val videoHeight = it.videoHeight
            LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$videoWidth mVideoHeight=$videoHeight")

            listener(videoWidth, videoHeight)
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnCompletionListener")

        initPlayer()
        mMediaPlayer.setOnCompletionListener {
            LogUtil_d(this.javaClass.simpleName, "OnCompletionListener")

            mCurrentState = STATE_COMPLETED

            listener()
        }
    }

    override fun setOnBufferingUpdateListener(listener: (percent: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnBufferingUpdateListener")

        initPlayer()
        mMediaPlayer.setOnBufferingUpdateListener { _, percent: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnBufferingUpdateListener")

            listener(percent)
        }
    }

    override fun setOnSeekCompleteListener(listener: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnSeekCompleteListener")

        initPlayer()
        mMediaPlayer.setOnSeekCompleteListener {
            LogUtil_d(this.javaClass.simpleName, "OnSeekCompleteListener")

            listener()
        }
    }

    override fun setOnVideoSizeChangedListener(listener: (width: Int, height: Int, sar_num: Int, sar_den: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnVideoSizeChangedListener")

        initPlayer()
        mMediaPlayer.setOnVideoSizeChangedListener { _: IMediaPlayer, width: Int, height: Int, sar_num: Int, sar_den: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnVideoSizeChangedListener")

            listener(width, height, sar_num, sar_den)
        }
    }

    override fun setOnErrorListener(listener: (msg: String) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnErrorListener")

        initPlayer()
        mMediaPlayer.setOnErrorListener { _: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnErrorListener[what=$what,extra=$extra]")

            mCurrentState = STATE_ERROR

            listener("未知错误")
            true
        }
    }

    override fun setOnInfoListener(listener: (what: Int, extra: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnInfoListener")

        initPlayer()
        mMediaPlayer.setOnInfoListener { _: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnInfoListener[what=$what extra=$extra]")

            listener(what, extra)
            false
        }
    }

    override fun setOnTimedTextListener(listener: (text: TimedText) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnTimedTextListener")

        initPlayer()
        mMediaPlayer.setOnTimedTextListener { _: IMediaPlayer, text: IjkTimedText ->
            LogUtil_d(this.javaClass.simpleName, "OnTimedTextListener")

            listener(TimedText(text.bounds, text.text))
        }
    }

    override fun clearAllListener() {
        LogUtil_d(this.javaClass.simpleName, "clearAllListener")

        initPlayer()
        mOnlyOperator = null
        if (mMediaPlayer is AbstractMediaPlayer) {
            (mMediaPlayer as AbstractMediaPlayer).resetListeners()
        }
    }
}