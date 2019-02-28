package com.ly.app.video

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.ly.pub.util.LogUtil_d
import java.io.File


/**
 * Created by LanYang on 2019/2/12
 */
class VideoLoader(private val context: Context, private val callback: VideoLoaderCallback) :
    LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            context,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT
            ),
            MediaStore.Video.Media.SIZE + ">?",
            arrayOf("0"),
            MediaStore.Video.Media.DATE_TAKEN + " DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        LogUtil_d(this::class.java.simpleName, "onLoadFinished")

        if (data == null) return

        val list = ArrayList<VideoEntity>()

        if (data.moveToFirst()) {
            do {
                val id = data.getLong(data.getColumnIndex(MediaStore.Video.Media._ID))
                val thumbCursor = context.contentResolver.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.WIDTH,
                        MediaStore.Video.Thumbnails.HEIGHT
                    ),
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=$id",
                    null,
                    null
                )
                var thumbPath = ""
                if (thumbCursor != null && thumbCursor.moveToFirst()) {
                    thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                    val file = File(thumbPath)
                    if (!file.exists()) LogUtil_d(this::class.java.simpleName, "File=$thumbPath 不存在")
                }
                thumbCursor?.close()

                val videoEntity = VideoEntity(
                    id = id,
                    path = data.getString(data.getColumnIndex(MediaStore.Video.Media.DATA)),
                    duration = data.getLong(data.getColumnIndex(MediaStore.Video.Media.DURATION)),
                    name = data.getString(data.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                    width = data.getLong(data.getColumnIndex(MediaStore.Video.Media.WIDTH)),
                    height = data.getLong(data.getColumnIndex(MediaStore.Video.Media.HEIGHT)),
                    thumbPath = thumbPath
                )
                LogUtil_d(this::class.java.simpleName, videoEntity.toString())

                list.add(videoEntity)

            } while (data.moveToNext())
        }

        callback.onLoadFinished(list)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        LogUtil_d(this::class.java.simpleName, "onLoaderReset")
        callback.onLoaderReset(loader)
    }
}

interface VideoLoaderCallback {
    fun onLoadFinished(list: List<VideoEntity>)
    fun onLoaderReset(loader: Loader<Cursor>)
}

data class VideoEntity(
    /**视频索引*/
    val id: Long,
    /**视频路径*/
    val path: String,
    /**视频时长*/
    val duration: Long,
    /**视频名称*/
    val name: String,
    /**视频宽度*/
    val width: Long,
    /**视频高度*/
    val height: Long,
    /**视频缩略图*/
    val thumbPath: String
) {
    override fun toString(): String {
        return "[id=$id path=$path duration=$duration name=$name width=$width height=$height thumbPath=$thumbPath]"
    }
}