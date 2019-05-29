package com.ly.video

import android.content.Context
import com.ly.pub.PUBLIC_APPLICATION
import com.ly.video.annotation.CAPTURE_CONFIG_MID
import com.ly.video.annotation.CaptureConfig
import com.ly.video.annotation.GIF_FPS_10
import com.ly.video.annotation.GifFps
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

    /**是否显示悬浮窗口;默认true,显示窗口*/
    private var mIsShowSusWindow: Boolean

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
        mIsShowSusWindow = mSharedPre.getSharedPreBoolean(VIDEO_PARAMS_SHOW_SUS_WINDOW, true)
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


    fun setShowSusWindow(flag: Boolean): VideoManager {
        mIsShowSusWindow = flag
        mSharedPre.putSharedPreBoolean(VIDEO_PARAMS_SHOW_SUS_WINDOW, flag)
        return this
    }

    fun isShowSusWindow() = mIsShowSusWindow


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

    @CaptureConfig
    fun getCapGifConfig() = mCapGifConfig


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

const val VIDEO_PARAMS_SHOW_SUS_WINDOW = "show_sus_window"

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