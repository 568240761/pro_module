package com.ly.app.video

import com.ly.image.glide.GlideImpl
import com.ly.pub.PUBLIC_IMAGE_LOADER
import com.ly.pub.PUBLIC_IS_LOG
import com.ly.pub.PubApplication

/**
 * Created by LanYang on 2019/2/12
 */
class VideoApplication : PubApplication() {
    override fun onCreate() {
        super.onCreate()
        PUBLIC_IS_LOG = BuildConfig.DEBUG
        PUBLIC_IMAGE_LOADER = GlideImpl
    }
}