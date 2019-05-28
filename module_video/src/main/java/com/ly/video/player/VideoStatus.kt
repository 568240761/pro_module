package com.ly.video.player

import androidx.annotation.IntDef

/**
 * Created by LanYang on 2019/5/28
 */

const val STATE_ERROR = -1
const val STATE_IDLE = STATE_ERROR + 1
const val STATE_PREPARING = STATE_IDLE + 1
const val STATE_PREPARED = STATE_PREPARING + 1
const val STATE_PLAYING = STATE_PREPARED + 1
const val STATE_PAUSED = STATE_PLAYING + 1
const val STATE_STOP = STATE_PAUSED + 1
const val STATE_COMPLETED = STATE_STOP + 1

@IntDef(value = [STATE_ERROR, STATE_IDLE, STATE_PREPARING, STATE_PREPARED, STATE_PLAYING, STATE_PAUSED, STATE_STOP, STATE_COMPLETED])
@Retention(AnnotationRetention.SOURCE)
annotation class VideoStatus