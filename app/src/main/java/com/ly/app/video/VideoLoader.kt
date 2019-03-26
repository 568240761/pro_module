package com.ly.app.video

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.LogUtil_i
import java.io.File
import java.util.*

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
                MediaStore.Video.Media.BOOKMARK,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.TITLE
            ),
            MediaStore.Video.Media.SIZE + ">?",
            arrayOf("0"),
            MediaStore.Video.Media.DATE_TAKEN + " DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        LogUtil_d(this::class.java.simpleName, "onLoadFinished")

        if (data == null) return

        val list = ArrayList<SourceEntity>()

        if (data.moveToFirst()) {
            do {
                val id = data.getLong(data.getColumnIndex(MediaStore.Video.Media._ID))
                val thumbCursor = context.contentResolver.query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Video.Thumbnails.DATA
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
                } else {
                    LogUtil_i(this::class.java.simpleName, "id=${id}的视频图片未查找到")
                }
                thumbCursor?.close()

                val videoEntity = SourceEntity(
                    type = 0,
                    id = id,
                    path = data.getString(data.getColumnIndex(MediaStore.Video.Media.DATA)),
                    duration = data.getLong(data.getColumnIndex(MediaStore.Video.Media.DURATION)),
                    name = data.getString(data.getColumnIndex(MediaStore.Video.Media.TITLE)),
                    thumbPath = thumbPath,
                    bookmark = data.getInt(data.getColumnIndex(MediaStore.Video.Media.BOOKMARK)),
                    bucket = data.getString(data.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)),
                    resolution = data.getString(data.getColumnIndex(MediaStore.Video.Media.RESOLUTION))
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
    fun onLoadFinished(list: List<SourceEntity>)
    fun onLoaderReset(loader: Loader<Cursor>)
}

data class SourceEntity(
    /**类型;0-视频,1-广告*/
    val type: Int,
    /**视频索引*/
    val id: Long,
    /**视频路径*/
    val path: String,
    /**视频时长*/
    val duration: Long,
    /**视频名称*/
    val name: String,
    /**视频缩略图*/
    val thumbPath: String,
    /**已播放时长*/
    val bookmark: Int,
    /**视频所在上级文件目录*/
    val bucket: String,
    /**视频宽高*/
    val resolution: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {}

    /**
     * 过滤掉文件后缀的字段
     */
    fun getVideoName(): String {
        return if (name.contains(".")) {
            val index = name.indexOf(".")
            name.removeRange(index, name.length)
        } else
            name
    }

    override fun toString(): String {
        return "[id=$id path=$path duration=$duration name=$name thumbPath=$thumbPath bookmark=$bookmark bucket=$bucket resolution=$resolution]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeLong(id)
        parcel.writeString(path)
        parcel.writeLong(duration)
        parcel.writeString(name)
        parcel.writeString(thumbPath)
        parcel.writeInt(bookmark)
        parcel.writeString(bucket)
        parcel.writeString(resolution)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SourceEntity> {
        override fun createFromParcel(parcel: Parcel): SourceEntity {
            return SourceEntity(parcel)
        }

        override fun newArray(size: Int): Array<SourceEntity?> {
            return arrayOfNulls(size)
        }
    }
}