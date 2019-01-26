package com.ly.pub

/**
 * Created by LanYang on 2018/8/6
 * 网络请求接口
 */
interface PubNetworkManager {

    /**
     * 退出页面时，正在进行的或等待被执行的请求任务应该取消掉
     *
     * @param any 一般为业务对象
     */
    fun cancelTag(any: Any)

    /**
     * 取消单个网络请求
     *
     * @param any 一般为业务对象
     * @param key 网络请求唯一key
     */
    fun cancelSingleTag(any: Any, key: String)

    /**
     * Form表单形式的post请求
     * @param any 用于网络请求标记;如果为空,表明不需要自动取消网络请求
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 网络回调
     * @param listener 网络请求进度回调接口
     */
    fun post(
        any: Any?,
        url: String,
        params: Map<String, Any>? = null,
        callback: PubNetworkCallback? = null,
        listener: PubNetworkProgressListener? = null
    )

    /**
     * JSON形式的post请求
     * @param any 用于网络请求标记;如果为空,表明不需要自动取消网络请求
     * @param url 请求地址
     * @param common 拼接在url后常用字段
     * @param params 请求参数
     * @param callback 网络回调
     * @param listener 网络请求进度回调接口
     */
    fun postJson(
        any: Any?,
        url: String,
        common: Map<String, Any>,
        params: Map<String, Any>? = null,
        callback: PubNetworkCallback? = null,
        listener: PubNetworkProgressListener? = null
    )

    /**
     * 下载文件
     * @param any 用于网络请求标记;如果为空,表明不需要自动取消网络请求
     * @param url 请求地址
     * @param listener 网络请求进度回调接口
     * @param path 下载后保存的地址
     */
    fun downloadFile(
        any: Any?,
        url: String,
        listener: PubNetworkProgressListener,
        path: String
    )
}