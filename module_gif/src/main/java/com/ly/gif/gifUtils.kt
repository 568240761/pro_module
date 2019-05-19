package com.ly.gif

import android.graphics.Bitmap
import com.ly.gif.encoder.GIFEncoder
import com.ly.pub.util.LogUtil_d
import java.io.File

/**
 * Created by LanYang on 2019/5/19
 */

/**
 * 生成GIF文件
 *
 * @param bitmapList 位图集合
 * @param name GIF文件名称(包含路径)
 * @param delay 延时时间(ms)
 * @param fps 帧率(用于生成视频gif文件)
 * @param failure 生成GIF文件失败回调函数
 * @param success 生成GIF文件成功回调函数
 */
fun generateGif(
    bitmapList: List<Bitmap>,
    name: String,
    delay: Int = 0,
    fps: Float = 0f,
    failure: () -> Unit = {},
    success: (file: File) -> Unit = {}
) {

    LogUtil_d("generateGif","size=${bitmapList.size}")

    if (bitmapList.size <= 1) {
        failure()
        return
    }

    val file = File(name)
    if (!file.parentFile.exists()) file.parentFile.mkdirs()
    if (!file.exists()) file.createNewFile()

    val gifEncoder = GIFEncoder()
    if (fps > 0) gifEncoder.setFrameRate(fps)
    if (delay > 0) gifEncoder.setDelay(delay)
    gifEncoder.init(bitmapList[0])
    gifEncoder.start(file)
    for (i in bitmapList.indices) {
        if (i == 0) continue
        gifEncoder.addFrame(bitmapList[i])
    }
    gifEncoder.finish()
    success(file)
}