package com.ly.video.annotation

import androidx.annotation.IntDef

/**
 * Created by LanYang on 2019/5/29
 *
 * 关于截图质量的常量
 */

/**对应[android.graphics.Bitmap.Config.ALPHA_8]*/
const val CAPTURE_CONFIG_LOW = 0
/**对应[android.graphics.Bitmap.Config.RGB_565]*/
const val CAPTURE_CONFIG_MID = CAPTURE_CONFIG_LOW + 1
/**对应[android.graphics.Bitmap.Config.ARGB_8888]*/
const val CAPTURE_CONFIG_HEIGHT = CAPTURE_CONFIG_MID + 1

@IntDef(value = [CAPTURE_CONFIG_LOW, CAPTURE_CONFIG_MID, CAPTURE_CONFIG_HEIGHT])
@Retention(AnnotationRetention.SOURCE)
annotation class CaptureConfig

