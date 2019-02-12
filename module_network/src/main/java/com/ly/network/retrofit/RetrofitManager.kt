package com.ly.network.retrofit

import androidx.collection.ArrayMap
import com.ly.pub.PUBLIC_IS_LOG
import com.ly.pub.PUBLIC_SESSION_ID
import com.ly.pub.PubNetworkCallback
import com.ly.pub.PubNetworkProgressListener
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.gsonMapToString
import com.ly.pub.util.hashUrl
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by LanYang on 2018/8/6
 */

class RetrofitManager {
    companion object {
        lateinit var okHttpClient: OkHttpClient
    }

    /**超时时间(毫秒)*/
    private val TIME_OUT = 60000L
    /**记录网络请求*/
    private val mTagCall = ArrayMap<Any, ArrayMap<String, Call<ResponseBody>>>()

    private lateinit var mRetrofit: Retrofit

    private val mProgressInterceptor = ProgressInterceptor()

    private val mRetrofitRequest: RetrofitRequest by lazy {
        createNetworkManager(RetrofitRequest::class.java)
    }

    private fun <T> createNetworkManager(clazz: Class<T>): T {
        val mOkHttpClientBuilder = OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(getLogInterceptor())
                .addNetworkInterceptor(mProgressInterceptor)

        okHttpClient = mOkHttpClientBuilder.build()

        mRetrofit = Retrofit.Builder()
                .baseUrl("http://www.aiqin.com")
                .client(okHttpClient)
                .build()
        return mRetrofit.create(clazz)
    }

    private fun getLogInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor(RetrofitLogger(PUBLIC_IS_LOG))
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    /**
     * 取消网络请求
     * @param any 网络请求标记(通常为业务实例)
     */
    fun cancel(any: Any) {
        val value = mTagCall.get(any)
        if (value != null) {
            for (call in value.values) {
                call.cancel()
            }
        }
        mTagCall.remove(any)
    }

    /**
     * 取消单个网络请求
     * @param any 网络请求标记(通常为业务实例)
     * @param key 网络请求唯一key
     */
    fun cancelSingle(any: Any, key: String) {
        val value = mTagCall.get(any)
        if (value != null) {
            val call = value[key]
            if (call != null) {
                call.cancel()
                value.remove(key)
            }
        }
    }

    /**
     * 下载文件
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param listener 网络请求进度回调对象
     * @param path 文件保存路径
     */
    fun downloadFile(any: Any?, url: String, listener: PubNetworkProgressListener, path: String) {
        if (addUrlKey(any, url, null, null)) {
            resetProgressInterceptor(listener, path)
            val client = okHttpClient.newBuilder()
                    .readTimeout(10, TimeUnit.MINUTES)
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .writeTimeout(10, TimeUnit.MINUTES).build()
            val retrofitClone = mRetrofit.newBuilder().client(client).build()
            val call = retrofitClone.create(RetrofitRequest::class.java).downloadFile(url)
            addUrlKey(any, url, null, call)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    deleteFile(path)
                    if (call!!.isCanceled) {
                        LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                        return
                    }
                    listener.onFail()
                    removeUrlKey(any, url, null)
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                    try {
                        if (response != null) {
                            val body = response.body()
                            if (body == null) {
                                deleteFile(path)
                                if (call!!.isCanceled) {
                                    LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                                    return
                                }
                                listener.onFail()
                            } else {
                                if (call!!.isCanceled) {
                                    LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                                    return
                                }
                                listener.onSuccess()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    removeUrlKey(any, url, null)
                }
            })
        } else {
            LogUtil_d("RetrofitManager", "存在相同的接口与参数正在执行！")
        }
    }

    private fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * Form表单形式的post请求(总部项目常用网络请求)
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param params 请求参数
     * @param callback 网络请求回调
     * @param listener 网络请求进度回调对象
     */
    fun post(
        any: Any?,
        url: String,
        params: Map<String, Any>?,
        callback: PubNetworkCallback?,
        listener: PubNetworkProgressListener?
    ) {
        if (addUrlKey(any, url, params)) {
            resetProgressInterceptor(listener = listener)
            val call = if (PUBLIC_SESSION_ID.isNotEmpty()) {
                if (params == null) {
                    mRetrofitRequest.post(PUBLIC_SESSION_ID, url)
                } else {
                    mRetrofitRequest.post(PUBLIC_SESSION_ID, url, params)
                }
            } else {
                if (params == null) {
                    mRetrofitRequest.post(url)
                } else {
                    mRetrofitRequest.post(url, params)
                }
            }
            callback?.before()
            addUrlKey(any, url, params, call)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    if (call!!.isCanceled) {
                        LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                        return
                    }
                    fail(any, url, params, callback)
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (call!!.isCanceled) {
                        LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                        return
                    }
                    success(any, url, params, response, callback)
                }
            })
        } else {
            LogUtil_d("RetrofitManager", "存在相同的接口与参数正在执行！")
        }
    }

    /**
     * JSON形式的post请求(北苑项目常用网络请求)
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param common 拼接在url后常用字段
     * @param params 请求参数
     * @param callback 网络请求回调
     * @param listener 网络请求进度回调对象
     */
    fun postJson(
            any: Any?,
            url: String,
            common: Map<String, Any>,
            params: Map<String, Any>?,
            callback: PubNetworkCallback?,
            listener: PubNetworkProgressListener?
    ) {
        if (addUrlKey(any, url, params)) {
            resetProgressInterceptor(listener = listener)

            val call = if (params == null) {//请求参数为空时，变为get请求
                mRetrofitRequest.get(url, common)
            } else {
                val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gsonMapToString(params))
                mRetrofitRequest.postJson(url, common, requestBody)
            }
            callback?.before()

            addUrlKey(any, url, params, call)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    if (call!!.isCanceled) {
                        LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                        return
                    }
                    fail(any, url, params, callback)
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (call!!.isCanceled) {
                        LogUtil_d("RetrofitManager", "网络请求已经被取消!")
                        return
                    }
                    success(any, url, params, response, callback)
                }
            })
        } else {
            LogUtil_d("RetrofitManager", "存在相同的接口与参数正在执行！")
        }

    }

    /**
     * 重置网络请求进度参数
     * @param listener 网络请求进度回调对象
     * @param path 文件保存位置;如果不需要下载文件,为空字符串
     */
    private fun resetProgressInterceptor(
            listener: PubNetworkProgressListener? = null,
            path: String = ""
    ) {
        mProgressInterceptor.mListener = listener
        mProgressInterceptor.path = path
    }

    /**
     * 响应失败
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param params 请求参数
     * @param callback 网络请求回调
     */
    private fun fail(
            any: Any?,
            url: String,
            params: Map<String, Any>?,
            callback: PubNetworkCallback?
    ) {
        LogUtil_d(this.javaClass.simpleName,"fail-网络请求失败")

        callback?.fail()
        callback?.after()

        removeUrlKey(any, url, params)
    }

    /**
     * 响应成功，解析数据
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param params 请求参数
     * @param response 网络请求响应内容
     * @param callback 网络请求回调
     */
    private fun success(
            any: Any?,
            url: String,
            params: Map<String, Any>?,
            response: Response<ResponseBody>?,
            callback: PubNetworkCallback?
    ) {

        val str = response?.body()?.string()

        if (!str.isNullOrEmpty()) {//状态码正常时
            LogUtil_d(this.javaClass.simpleName,"success-状态码正常")
            callback?.parseResponse(str)
        } else {//状态码不正常时
            LogUtil_d(this.javaClass.simpleName,"success-状态码不正常")
            callback?.parseResponse(response?.errorBody()?.string())
        }
        callback?.after()

        removeUrlKey(any, url, params)
    }

    /**
     * 记录网络请求
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param params 请求参数
     * @param call 网络请求实例;如果为空,该方法仅作为判断是否有相同接口正在执行;如果不为空且没有相同接口正在执行,记录该网络请求
     * @return true,表示没有相同接口正在执行;false,表明有相同的接口正在执行
     */
    private fun addUrlKey(
            any: Any?,
            url: String,
            params: Map<String, Any>?,
            call: Call<ResponseBody>? = null
    ): Boolean {
        var flag = true
        if (any == null) return flag
        val value = mTagCall[any]
        val key = if (params == null) hashUrl(url) else hashUrl(url + params.toString())
        if (call != null) LogUtil_d("urlKey", "Manager.add:$key")
        if (value != null) {
            if (value[key] == null) {
                value[key] = call
            } else {
                flag = false
            }
        } else {
            val newValue = ArrayMap<String, Call<ResponseBody>>()
            newValue[key] = call
            mTagCall[any] = newValue
        }
        return flag
    }

    /**
     * 移除记录的网络请求
     * @param any 通常为业务实例;如果为空,表明不需要自动取消网络请求
     * @param url 接口
     * @param params 请求参数
     */
    private fun removeUrlKey(
            any: Any?,
            url: String,
            params: Map<String, Any>?
    ) {
        if (any == null) return
        val value = mTagCall.get(any)
        val key = if (params == null) hashUrl(url) else hashUrl(url + params.toString())
        LogUtil_d("urlKey", "Manager.remove:$key")
        if (value != null) {
            if (value[key] != null) {
                value.remove(key)
            }
            if (value.size == 0) {
                mTagCall.remove(any)
            }
        }
    }
}