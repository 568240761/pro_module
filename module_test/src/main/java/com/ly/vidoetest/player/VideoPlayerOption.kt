package com.ly.vidoetest.player

import android.net.Uri
import com.ly.vidoetest.render.IRenderView

/**
 * Created by LanYang on 2019/3/28
 * 视频播放器选项
 */
class VideoPlayerOption {
    /**视频源*/
    private lateinit var mUri: Uri
    /**与数据请求一起发送的标头(网络视频)*/
    private val mHeader = HashMap<String, String>()
    /**视频画面控件*/
    private lateinit var mRender: IRenderView
    /**开发状态*/
    private var mIsDebug = false

    fun setUri(uri: Uri): VideoPlayerOption {
        mUri = uri
        return this
    }

    fun setHeader(header: Map<String, String>? = null): VideoPlayerOption {
        mHeader.clear()
        if (header != null) mHeader.putAll(header)
        return this
    }

    fun setRender(render: IRenderView): VideoPlayerOption {
        mRender = render
        return this
    }

    fun setDebug(isDebug: Boolean): VideoPlayerOption {
        mIsDebug = isDebug
        return this
    }

    internal fun build(): IVideoPlayer {
        val player = BaseVideoPlayer()
        player.setDebug(mIsDebug)

        if (this::mRender.isInitialized)
            player.bindRender(mRender)
        else
            throw IllegalArgumentException("IVideoPlayer实例需绑定IRenderView实例")

        if (this::mUri.isInitialized)
            player.changeVideoURI(mUri, mHeader)
        else
            throw IllegalAccessException("视频源不能为空")

        return player
    }
}