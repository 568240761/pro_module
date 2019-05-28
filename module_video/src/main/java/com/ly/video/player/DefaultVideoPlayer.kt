package com.ly.video.player

import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkTimedText

/**
 * Created by LanYang on 2019/5/28
 */
class DefaultVideoPlayer : IVideoPlayer {

    @VideoStatus
    private var mCurrentState = STATE_IDLE

    private val mMediaPlayer: IMediaPlayer = IjkMediaPlayer()

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

    override fun setVideoURI(uri: Uri, headers: Map<String, String>?) {
        LogUtil_d(this.javaClass.simpleName, "Uri=$uri Header=${headers?.toString()}")

        mMediaPlayer.setDataSource(PUBLIC_APPLICATION, uri, headers)
    }

    override fun setDisplay(holder: SurfaceHolder?) {
        mMediaPlayer.setDisplay(holder)
    }

    override fun setSurface(surface: Surface?) {
        mMediaPlayer.setSurface(surface)
    }

    override fun prepareAsync(prepared: () -> Unit, completion: () -> Unit, error: (msg: String) -> Unit) {
        mMediaPlayer.prepareAsync()
        mCurrentState = STATE_PREPARING

        setOnCompletionListener(prepared)
        setOnCompletionListener(completion)
        setOnErrorListener(error)
    }

    override fun getWidth(): Int {
        return if (isPreparedState()) mMediaPlayer.videoWidth else 0
    }

    override fun getHeight(): Int {
        return if (isPreparedState()) mMediaPlayer.videoHeight else 0
    }

    override fun start(other: () -> Unit) {
        if (isPreparedState()) {
            LogUtil_d(this.javaClass.simpleName, "start")

            mMediaPlayer.start()
            mCurrentState = STATE_PLAYING

            other()
        }
    }

    override fun pause(other: () -> Unit) {
        if (isPlaying()) {
            LogUtil_d(this.javaClass.simpleName, "pause")

            mMediaPlayer.pause()
            mCurrentState = STATE_PAUSED

            other()
        }
    }

    override fun stop(other: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "stop")

        mMediaPlayer.stop()
        mCurrentState = STATE_STOP

        other()
    }

    override fun release(other: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "release")

        mMediaPlayer.release()
        mCurrentState = STATE_IDLE

        other()
    }

    override fun destroy(other: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "destroy")

        IjkMediaPlayer.native_profileEnd()

        other()
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

    override fun setOnPreparedListener(listener: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnPreparedListener")

        mMediaPlayer.setOnPreparedListener {
            LogUtil_d(this.javaClass.simpleName, "IMediaPlayer.OnPreparedListener")

            mCurrentState = STATE_PREPARED

            val videoWidth = it.videoWidth
            val videoHeight = it.videoHeight
            LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$videoWidth mVideoHeight=$videoHeight")

//            if (videoWidth > 0 && videoHeight > 0) {
//                mRenderView.setVideoSize(videoWidth, videoHeight)
//            }

            listener()
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnCompletionListener")

        mMediaPlayer.setOnCompletionListener {
            LogUtil_d(this.javaClass.simpleName, "OnCompletionListener")

            mCurrentState = STATE_COMPLETED

            listener()
        }
    }

    override fun setOnBufferingUpdateListener(listener: (percent: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnBufferingUpdateListener")

        mMediaPlayer.setOnBufferingUpdateListener { _, percent: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnBufferingUpdateListener")

            listener(percent)
        }
    }

    override fun setOnSeekCompleteListener(listener: () -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnSeekCompleteListener")

        mMediaPlayer.setOnSeekCompleteListener {
            LogUtil_d(this.javaClass.simpleName, "OnSeekCompleteListener")

            listener()
        }
    }

    override fun setOnVideoSizeChangedListener(listener: (width: Int, height: Int, sar_num: Int, sar_den: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnVideoSizeChangedListener")

        mMediaPlayer.setOnVideoSizeChangedListener { _: IMediaPlayer, width: Int, height: Int, sar_num: Int, sar_den: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnVideoSizeChangedListener")

            listener(width, height, sar_num, sar_den)
        }
    }

    override fun setOnErrorListener(listener: (msg: String) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnErrorListener")

        mMediaPlayer.setOnErrorListener { _: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnErrorListener[what=$what,extra=$extra]")

            mCurrentState = STATE_ERROR

            listener("")
            true
        }
    }

    override fun setOnInfoListener(listener: (what: Int, extra: Int) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnInfoListener")

        mMediaPlayer.setOnInfoListener { _: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this.javaClass.simpleName, "OnInfoListener")

            listener(what, extra)
            false
        }
    }

    override fun setOnTimedTextListener(listener: (text: TimedText) -> Unit) {
        LogUtil_d(this.javaClass.simpleName, "setOnTimedTextListener")

        mMediaPlayer.setOnTimedTextListener { _: IMediaPlayer, text: IjkTimedText ->
            LogUtil_d(this.javaClass.simpleName, "OnTimedTextListener")

            listener(TimedText(text.bounds, text.text))
        }
    }
}