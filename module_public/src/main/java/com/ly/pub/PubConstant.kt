package com.ly.pub

import android.app.Application

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

/**网络管理实例*/
lateinit var PUBLIC_NETWORK_MANAGER: PubNetworkManager