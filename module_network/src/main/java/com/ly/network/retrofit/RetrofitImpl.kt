package com.ly.network.retrofit

import com.ly.pub.PubNetworkCallback
import com.ly.pub.PubNetworkManager
import com.ly.pub.PubNetworkProgressListener

/**
 * Created by LanYang on 2018/8/6
 * 饿汉单例
 */

object RetrofitImpl : PubNetworkManager {
    private var mRetrofitManager: RetrofitManager = RetrofitManager()

    override fun cancelTag(any: Any) {
        mRetrofitManager.cancel(any)
    }

    override fun cancelSingleTag(any: Any, key: String) {
        mRetrofitManager.cancelSingle(any, key)
    }

    override fun post(any: Any?, url: String, params: Map<String, Any>?, callback: PubNetworkCallback?, listener: PubNetworkProgressListener?) {
        mRetrofitManager.post(any, url, params, callback, listener)
    }

    override fun postJson(any: Any?, url: String, common: Map<String, Any>, params: Map<String, Any>?, callback: PubNetworkCallback?, listener: PubNetworkProgressListener?) {
        mRetrofitManager.postJson(any, url, common, params, callback, listener)
    }

    override fun downloadFile(any: Any?, url: String, listener: PubNetworkProgressListener, path: String) {
        mRetrofitManager.downloadFile(any, url, listener, path)
    }
}