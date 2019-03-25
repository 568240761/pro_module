package com.ly.video

import android.os.Handler
import android.os.Message


/**
 * Created by LanYang on 2019/3/25
 */

class VideoHandler(private val ivHandler: IVideoHandler) : Handler() {

    override fun handleMessage(msg: Message?) {
        ivHandler.handleMessage(msg)
    }
}

interface IVideoHandler {
    fun handleMessage(msg: Message?)
}