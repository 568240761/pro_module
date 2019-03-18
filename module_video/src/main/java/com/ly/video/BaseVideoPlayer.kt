package com.ly.video

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_e
import tv.danmaku.ijk.media.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Created by LanYang on 2019/2/27
 * 视频播放器;使用该类需要实现带UI控制界面[LYVideoPlayer]
 */
open class BaseVideoPlayer : FrameLayout, IVideoPlayer {

    private val STATE_ERROR = -1
    private val STATE_IDLE = 0
    private val STATE_PREPARING = 1
    private val STATE_PREPARED = 2
    private val STATE_PLAYING = 3
    private val STATE_PAUSED = 4
    private val STATE_PLAYBACK_COMPLETED = 5

    private var mCurrentState = STATE_IDLE

    private var mOnErrorListener: IMediaPlayer.OnErrorListener? = null
    private var mOnCompletionListener: IMediaPlayer.OnCompletionListener? = null
    private var mOnPreparedListener: IMediaPlayer.OnPreparedListener? = null
    private var mVideoSizeChangedListener: IMediaPlayer.OnVideoSizeChangedListener? = null

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0

    private var mRenderView: IRenderView? = null

    private val mMediaPlayer: IMediaPlayer = IjkMediaPlayer()

    private val mAudioManager = PUBLIC_APPLICATION.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    constructor(context: Context) : super(context) {
        this.init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        this.init()
    }

    private fun init() {
        setOnPreparedListener()
        setOnCompletionListener()
        setOnVideoSizeChangedListener()
        setOnErrorListener()

        IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        if (mMediaPlayer is IjkMediaPlayer) {
            //硬解码
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1)
//            mMediaPlayer.setOption(
//                IjkMediaPlayer.OPT_CATEGORY_PLAYER,
//                "overlay-format",
//                IjkMediaPlayer.SDL_FCC_RV32.toLong()
//            )
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0)
//            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48)
        }

        mRenderView = SurfaceRenderView(context)
        addView(mRenderView!!.getView())
    }

    override fun bindSurfaceHolder(holder: SurfaceHolder?) {
        mMediaPlayer.setDisplay(holder)
    }

    override fun setVideoURI(uri: Uri, headers: Map<String, String>?, isDebug: Boolean) {
        if (isDebug) IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)

        LogUtil_d(this.javaClass.simpleName, "Uri=$uri Header=${headers?.toString()}")

        mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        try {
            mMediaPlayer.setDataSource(context, uri, headers)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.setScreenOnWhilePlaying(true)
            mMediaPlayer.prepareAsync()
            mCurrentState = STATE_PREPARING
        } catch (e: Exception) {
            LogUtil_e(e)
            mOnErrorListener?.onError(mMediaPlayer, IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    override fun start() {
        if (isPreparedState()) {
            mMediaPlayer.start()
            mCurrentState = STATE_PLAYING
        }
    }

    override fun pause() {
        if (isPreparedState()) {
            if (mMediaPlayer.isPlaying) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
    }

    override fun canBackground() {
        VideoPlayerService.mediaPlayer = mMediaPlayer
        VideoPlayerService.intentToStart(context)
    }

    override fun stopBackground() {
        VideoPlayerService.mediaPlayer = null
        VideoPlayerService.intentToStop(context)
    }

    override fun getDuration(): Int {
        return if (isPreparedState()) {
            mMediaPlayer.duration.toInt()
        } else -1
    }

    override fun getCurrentPosition(): Int {
        return if (isPreparedState()) {
            mMediaPlayer.currentPosition.toInt()
        } else 0
    }

    override fun seekTo(pos: Long) {
        if (isPreparedState()) mMediaPlayer.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return isPreparedState() && mMediaPlayer.isPlaying
    }

    override fun captureFrame() {
    }

    override fun captureFrames() {
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtil_d(this.javaClass.simpleName,"onDetachedFromWindow")
        mMediaPlayer.stop()
        mMediaPlayer.release()
        mCurrentState = STATE_IDLE
        mAudioManager.abandonAudioFocus(null)
        IjkMediaPlayer.native_profileEnd()
    }

    fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener? = null) {
        mOnPreparedListener = IMediaPlayer.OnPreparedListener {
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "IMediaPlayer.OnPreparedListener")
            mCurrentState = STATE_PREPARED

            mVideoWidth = it.videoWidth
            mVideoHeight = it.videoHeight
            LogUtil_d(this.javaClass.simpleName, "mVideoWidth=$mVideoWidth")
            LogUtil_d(this.javaClass.simpleName, "mVideoHeight=$mVideoHeight")

            if (mVideoWidth > 0 && mVideoHeight > 0) {
                mRenderView?.setVideoSize(mVideoWidth, mVideoHeight)
                mRenderView?.resetLayout()
                mRenderView?.bindMediaPlayer(this@BaseVideoPlayer)
            }

            listener?.onPrepared(it)
        }
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener)
    }

    fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener? = null) {
        mOnCompletionListener = IMediaPlayer.OnCompletionListener { mp: IMediaPlayer ->
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "OnCompletionListener")
            mCurrentState = STATE_PLAYBACK_COMPLETED
            listener?.onCompletion(mp)
        }
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener)
    }

    fun setOnBufferingUpdateListener(listener: IMediaPlayer.OnBufferingUpdateListener) {
        mMediaPlayer.setOnBufferingUpdateListener(listener)
    }

    fun setOnSeekCompleteListener(listener: IMediaPlayer.OnSeekCompleteListener) {
        mMediaPlayer.setOnSeekCompleteListener(listener)
    }

    fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener? = null) {
        mVideoSizeChangedListener =
            IMediaPlayer.OnVideoSizeChangedListener { mp: IMediaPlayer, width: Int, height: Int, sarNum: Int, sarDen: Int ->
                LogUtil_d(
                    this@BaseVideoPlayer.javaClass.simpleName,
                    "OnVideoSizeChangedListener[width=$width,height=$height,sarNum=$sarNum,sarDen=$sarDen]"
                )

                if (width != 0 && height != 0) {
                    mRenderView?.setVideoSize(width, height)
                    mRenderView?.resetLayout()
                }
                listener?.onVideoSizeChanged(mp, width, height, sarNum, sarDen)
            }
        mMediaPlayer.setOnVideoSizeChangedListener(listener)
    }

    fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener? = null) {
        mOnErrorListener = IMediaPlayer.OnErrorListener { mp: IMediaPlayer, what: Int, extra: Int ->
            LogUtil_d(this@BaseVideoPlayer.javaClass.simpleName, "OnErrorListener[what=$what,extra=$extra]")

            mCurrentState = STATE_ERROR
            listener?.onError(mp, what, extra)
            true
        }
        mMediaPlayer.setOnErrorListener(mOnErrorListener)
    }

    fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener) {
        mMediaPlayer.setOnInfoListener(listener)
    }

    fun setOnTimedTextListener(listener: IMediaPlayer.OnTimedTextListener) {
        mMediaPlayer.setOnTimedTextListener(listener)
    }

    fun clearListeners() {
        mOnErrorListener = null
        mOnCompletionListener = null
        mOnPreparedListener = null
        if (mMediaPlayer is AbstractMediaPlayer) mMediaPlayer.resetListeners()
    }

    private fun isPreparedState(): Boolean {
        return mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING
    }
}