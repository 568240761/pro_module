package com.ly.video.annotation

import androidx.annotation.IntDef

/**
 * Created by LanYang on 2019/5/29
 *
 * 关于GIF文件帧率常量
 */

const val GIF_FPS_10 = 10
const val GIF_FPS_30 = 30
const val GIF_FPS_60 = 60

@IntDef(value = [GIF_FPS_10, GIF_FPS_30, GIF_FPS_60])
@Retention(AnnotationRetention.SOURCE)
annotation class GifFps