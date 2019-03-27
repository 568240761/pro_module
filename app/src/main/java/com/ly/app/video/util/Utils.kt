package com.ly.app.video.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.ly.app.video.loader.SourceEntity

/**
 * Created by LanYang on 2019/3/21
 */

/**
 * 查询视频详细信息
 * @param context 上下环境
 * @param uri 视频uri
 * @return 未查询到返回null
 */
fun findSourceBeanFromUri(context: Context, uri: Uri): SourceEntity? {
    val cursor = context.contentResolver.query(
        uri, arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BOOKMARK,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.TITLE
        ), null, null, null
    )

    var sourceEntity: SourceEntity? = null
    if (cursor != null) {
        cursor.moveToFirst()
        sourceEntity = SourceEntity(
                type = 0,
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)),
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)),
                name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)),
                thumbPath = "",
                bookmark = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.BOOKMARK)),
                bucket = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)),
                resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
        )
    }
    cursor?.close()

    return sourceEntity
}