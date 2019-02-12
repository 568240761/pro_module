package com.ly.network.retrofit

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ly.pub.PubNetworkProgressListener
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by LanYang on 2018/7/11
 */
class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val mListener: PubNetworkProgressListener? = null,
    val path: String = ""
) : ResponseBody() {
    private val MSG_WHAT_PROGRESS = 0x1
    private var bufferedSource: BufferedSource? = null
    private var myHandler: Handler? = null
    private var fos: FileOutputStream? = null

    init {
        myHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when (msg!!.what) {
                    MSG_WHAT_PROGRESS -> mListener?.onProgress(msg.arg1)
                }
            }
        }

        if (path.isNotEmpty()) {
            val file = File(path)
            if (!file.exists()) {
                val parent = file.parentFile
                if (!parent.exists()) {
                    parent.mkdirs()
                }
            }
            fos = FileOutputStream(file, true)
        }
    }

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {

        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(object : ForwardingSource(responseBody.source()) {
                var totalBytesRead = 0L
                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) {
                        if (fos != null) {
                            sink.writeTo(fos)
                        }
                        bytesRead
                    } else {
                        fos?.close()
                        0
                    }
                    val msg = Message.obtain()
                    msg.what = MSG_WHAT_PROGRESS
                    msg.arg1 = (totalBytesRead.toDouble() / contentLength().toDouble() * 100).toInt()
                    myHandler!!.sendMessage(msg)
                    return bytesRead
                }
            })
        }
        return bufferedSource!!
    }
}