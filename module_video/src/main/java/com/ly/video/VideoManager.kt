package com.ly.video

import android.content.Context
import android.graphics.Bitmap
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.video.annotation.*
import com.ly.video.player.DefaultVideoPlayer
import com.ly.video.player.IVideoPlayer
import com.ly.video.suspension.DefaultSusWindow

/**
 * Created by LanYang on 2019/5/29
 *
 * 全局配置参数信息
 */
object VideoManager {

    val videoPlayer: IVideoPlayer by lazy {
        DefaultVideoPlayer()
    }

    private val mSharedPre = VideoSharedPre()

    /**是否自动跟踪生命周期,默认true;
     * 比如直接从播放视频页面回到桌面,视频自动停止播放;返回又自动开始播放
     *
     * 当[mIsShowSusWindow]为true时,该属性失效
     */
    private var mIsTrackLifecycle: Boolean

    /**是否显示悬浮窗口;默认true,显示窗口*/
    private var mIsShowSusWindow: Boolean

    /**当前屏幕的宽/悬浮窗口宽;默认为2,即(当前屏幕的宽:悬浮窗口宽=2:1)*/
    private var mSusWindowWidthFactor: Int

    /**悬浮窗口出现位置横坐标相对位移*/
    private var mSusWindowX: Int

    /**悬浮窗口出现位置纵坐标相对位移*/
    private var mSusWindowY: Int

    /** 悬浮窗口是否可以移动;默认true,可以移动*/
    private var mSusWindowMove: Boolean

    /**关于截去图片质量参数*/
    @CaptureConfig
    private var mCapBitmapConfig: Int

    /**关于生成GIF图片质量参数*/
    @CaptureConfig
    private var mCapGifConfig: Int

    /**GIF文件截取的视频时长*/
    private var mGifTotalTime: Long

    /**GIF文件的帧率*/
    @GifFps
    private var mGifFps: Int

    init {
        mIsTrackLifecycle = mSharedPre.getSharedPreBoolean(VIDEO_PARAMS_IS_TRACK_LIFECYCLE, true)
        mIsShowSusWindow = mSharedPre.getSharedPreBoolean(VIDEO_PARAMS_SHOW_SUS_WINDOW, true)
        mSusWindowWidthFactor = mSharedPre.getSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_WIDTH_FACTOR, 2)
        mSusWindowX = mSharedPre.getSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_X, 0)
        mSusWindowY = mSharedPre.getSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_Y, 0)
        mSusWindowMove = mSharedPre.getSharedPreBoolean(VIDEO_PARAMS_SUS_WINDOW_MOVE, true)
        mCapBitmapConfig = mSharedPre.getSharedPreInt(VIDEO_PARAMS_CAP_BITMAP_CONFIG, CAPTURE_CONFIG_MID)
        mCapGifConfig = mSharedPre.getSharedPreInt(VIDEO_PARAMS_CAP_GIF_CONFIG, CAPTURE_CONFIG_MID)
        mGifTotalTime = mSharedPre.getSharedPreLong(VIDEO_PARAMS_GIF_TOTAL_TIME, 5000L)
        mGifFps = mSharedPre.getSharedPreInt(VIDEO_PARAMS_GIF_FPS, GIF_FPS_10)
    }

    /**
     * 开发状态;为true时,显示播放log日志
     */
    private var mIsDebug: Boolean = false

    fun setDebug(debug: Boolean): VideoManager {
        mIsDebug = debug
        videoPlayer.setDebug(debug)
        return this
    }

    fun isDebug() = mIsDebug

    fun setTrackLifecycle(flag: Boolean): VideoManager {
        mIsTrackLifecycle = flag
        mSharedPre.putSharedPreBoolean(VIDEO_PARAMS_IS_TRACK_LIFECYCLE, flag)
        return this
    }

    fun isTrackLifecycle() = mIsTrackLifecycle

    fun setShowSusWindow(flag: Boolean): VideoManager {
        mIsShowSusWindow = flag
        mSharedPre.putSharedPreBoolean(VIDEO_PARAMS_SHOW_SUS_WINDOW, flag)
        return this
    }

    fun isShowSusWindow() = mIsShowSusWindow


    fun setSusWindowWidthFactor(factor: Int): VideoManager {
        mSusWindowWidthFactor = factor
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_WIDTH_FACTOR, factor)
        return this
    }

    fun getSusWindowWidthFactor() = mSusWindowWidthFactor


    fun setSusWindowX(x: Int): VideoManager {
        mSusWindowX = x
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_X, x)
        return this
    }

    fun getSusWindowX() = mSusWindowX

    fun setSusWindowY(y: Int): VideoManager {
        mSusWindowY = y
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_SUS_WINDOW_Y, y)
        return this
    }

    fun getSusWindowY() = mSusWindowY


    fun setSusWindowMove(isMove: Boolean): VideoManager {
        mSusWindowMove = isMove
        mSharedPre.putSharedPreBoolean(VIDEO_PARAMS_SUS_WINDOW_MOVE, isMove)
        return this
    }

    fun isSusWindowMove() = mSusWindowMove


    /**悬浮窗口UI类信息*/
    private var mSusWindowClazz: Class<*> = DefaultSusWindow::class.java

    fun setSusWindowUI(clazz: Class<*>?): VideoManager {
        if (clazz?.checkSusWindow() == true) {
            mSusWindowClazz = clazz
        }
        return this
    }

    fun getSusWindowUI() = mSusWindowClazz


    fun setCapBitmapConfig(@CaptureConfig config: Int): VideoManager {
        mCapBitmapConfig = config
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_CAP_BITMAP_CONFIG, config)
        return this
    }

    @CaptureConfig
    fun getCapBitmapConfig() = mCapBitmapConfig


    fun setCapGifConfig(@CaptureConfig config: Int): VideoManager {
        mCapGifConfig = config
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_CAP_GIF_CONFIG, config)
        return this
    }


    fun getCapGifConfig(): Bitmap.Config {
        return when (mCapBitmapConfig) {
            CAPTURE_CONFIG_LOW -> Bitmap.Config.ALPHA_8
            CAPTURE_CONFIG_MID -> Bitmap.Config.RGB_565
            else -> Bitmap.Config.ARGB_8888
        }
    }

    fun setGifTotalTime(time: Long): VideoManager {
        mGifTotalTime = time
        mSharedPre.putSharedPreLong(VIDEO_PARAMS_GIF_TOTAL_TIME, time)
        return this
    }

    fun getGifTotalTime() = mGifTotalTime

    fun setGifFps(@GifFps fps: Int): VideoManager {
        mGifFps = fps
        mSharedPre.putSharedPreInt(VIDEO_PARAMS_GIF_FPS, fps)
        return this
    }

    @GifFps
    fun getGifFps() = mGifFps
}

const val VIDEO_PARAMS_IS_TRACK_LIFECYCLE = "is_track_lifecycle"

const val VIDEO_PARAMS_SHOW_SUS_WINDOW = "show_sus_window"

const val VIDEO_PARAMS_SUS_WINDOW_WIDTH_FACTOR = "sus_window_width_factor"

const val VIDEO_PARAMS_SUS_WINDOW_X = "sus_window_x"

const val VIDEO_PARAMS_SUS_WINDOW_Y = "sus_window_y"

const val VIDEO_PARAMS_SUS_WINDOW_MOVE = "sus_window_move"

const val VIDEO_PARAMS_CAP_BITMAP_CONFIG = "cap_bitmap_config"

const val VIDEO_PARAMS_CAP_GIF_CONFIG = "cap_gif_config"

const val VIDEO_PARAMS_GIF_TOTAL_TIME = "gif_total_time"

const val VIDEO_PARAMS_GIF_FPS = "gif_fps"

private class VideoSharedPre {

    private val mSp = PUBLIC_APPLICATION.getSharedPreferences("video_params", Context.MODE_PRIVATE)

    private val mEditor = mSp.edit()

    fun getSharedPreString(key: String, defaultValue: String): String {
        val value = mSp.getString(key, defaultValue)
        return value ?: ""
    }

    fun putSharedPreString(key: String, value: String) {
        mEditor.putString(key, value).commit()
    }

    fun getSharedPreLong(key: String, defaultValue: Long): Long {
        return mSp.getLong(key, defaultValue)
    }

    fun putSharedPreLong(key: String, value: Long) {
        mEditor.putLong(key, value).commit()
    }

    fun getSharedPreInt(key: String, defaultValue: Int): Int {
        return mSp.getInt(key, defaultValue)
    }

    fun putSharedPreInt(key: String, value: Int) {
        mEditor.putInt(key, value).commit()
    }

    fun getSharedPreBoolean(key: String, defaultValue: Boolean): Boolean {
        return mSp.getBoolean(key, defaultValue)
    }

    fun putSharedPreBoolean(key: String, value: Boolean) {
        mEditor.putBoolean(key, value).commit()
    }
}