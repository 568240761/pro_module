package com.ly.video

import android.view.View

/**
 * Created by LanYang on 2019/3/1
 * 关于渲染画面的接口
 */
interface IRenderView {

    fun getView(): View

    fun resetLayout()

    fun bindMediaPlayer(player: IVideoPlayer)

    fun setVideoSize(videoWidth: Int, videoHeight: Int)
}