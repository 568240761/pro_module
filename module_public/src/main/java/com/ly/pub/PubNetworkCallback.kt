package com.ly.pub

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by LanYang on 2018/8/6
 * 网络回调接口
 */
interface PubNetworkCallback {

    /**解析返回的服务字段
     * @param response 从服务端返回的内容
     */
    fun parseResponse(response: String?)

    /**
     * 网络请求开始前，回调该方法
     */
    fun before()

    /**
     * 网络请求成功；响应状态正常(根据自身业务逻辑判断)，响应结果为空，回调该方法
     */
    fun success()

    /**
     * 网络请求成功；响应状态正常(根据自身业务逻辑判断)，响应结果为JSONObject，回调该方法
     */
    fun successAny(result: JSONObject)

    /**
     * 网络请求成功；响应状态正常(根据自身业务逻辑判断)，响应结果为JSONOArray，回调该方法
     */
    fun successArray(result: JSONArray)

    /**
     * 网络请求成功；响应状态不正常(根据自身业务逻辑判断)，回调该方法
     */
    fun successError(result: JSONObject?, errorMsg: String, status: Int)

    /**
     * 网络请求失败，回调该方法
     */
    fun fail()

    /**
     * 网络请求完成后，回调该方法
     */
    fun after()
}