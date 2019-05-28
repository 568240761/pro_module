package com.ly.video.player

import android.graphics.Rect

/**
 * Created by LanYang on 2019/5/28
 */
class TimedText(
    private val mTextBounds: Rect? = null,
    private val mTextChars: String? = null
) {

    fun getBounds(): Rect? {
        return this.mTextBounds
    }

    fun getText(): String? {
        return this.mTextChars
    }
}