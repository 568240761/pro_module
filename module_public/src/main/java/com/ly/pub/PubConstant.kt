package com.ly.pub

import android.app.Application
import androidx.annotation.DrawableRes

/**
 * Created by LanYang on 2019/1/26
 */

/**是否需要多个dex*/
var PUBLIC_IS_MULTI_DEX = false

/**是否打印日志*/
var PUBLIC_IS_LOG = true

/**全局的application实例*/
lateinit var PUBLIC_APPLICATION: Application

/**状态异常时，需要跳转到指定登录页面*/
lateinit var PUBLIC_LOGIN_ACTIVITY: Class<*>

/**图片加载实例*/
lateinit var PUBLIC_IMAGE_LOADER: PubImageLoader
/**默认占位图*/
@DrawableRes
var PUBLIC_IMAGE_PLACEHOLDER = R.drawable.pub_image_placeholder
/**默认拉取失败占位图*/
@DrawableRes
var PUBLIC_IMAGE_ERROR = R.drawable.pub_image_error

/**网络管理实例*/
lateinit var PUBLIC_NETWORK_MANAGER: PubNetworkManager

/**
 * 登录后,保存ID;(有的项目需要用到该ID,有的项目不需要)
 * 服务端鉴别身份的ID；
 * 首次网络请求会返回该ID，之后每次网络请求，都需上传该ID
 */
var PUBLIC_SESSION_ID = ""