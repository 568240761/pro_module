package com.ly.network.retrofit

import com.ly.pub.PubNetworkProgressListener
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by LanYang on 2018/9/27
 */
class ProgressInterceptor : Interceptor {
    /**网络进度的回调对象*/
    var mListener: PubNetworkProgressListener? = null
    /**下载文件;默认为空字符串,表示不需要下载文件*/
    var path: String = ""

    override fun intercept(chain: Interceptor.Chain?): Response {
        val response = chain!!.proceed(chain.request())
        return response.newBuilder().body(ProgressResponseBody(response.body()!!, mListener, path)).build()
    }
}