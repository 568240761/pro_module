package com.ly.image.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.ly.pub.PUBLIC_IS_LOG

/**
 * Created by LanYang on 2018/8/6
 */
@GlideModule
class GlideSettingModule:AppGlideModule(){

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        if(PUBLIC_IS_LOG) builder.setLogLevel(Log.VERBOSE)
        //设置缓存大小
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, (100 * 1024 * 1024).toLong()))
        //设置图片转换格式
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    }

    override fun isManifestParsingEnabled(): Boolean {
        //禁用清单解析
        return false
    }
}