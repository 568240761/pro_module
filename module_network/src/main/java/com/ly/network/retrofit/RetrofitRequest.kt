package com.ly.network.retrofit

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by LanYang on 2018/8/6
 */
interface RetrofitRequest {
    @GET
    fun get(@Url url: String, @QueryMap common: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET
    fun downloadFile(@Url url: String): Call<ResponseBody>

    @POST
    fun post(@Url url: String): Call<ResponseBody>

    @POST
    @FormUrlEncoded
    fun post(@Url url: String, @FieldMap map: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @POST
    fun post(@Header("Cookie") header: String, @Url url: String): Call<ResponseBody>

    @POST
    @FormUrlEncoded
    fun post(@Header("Cookie") header: String, @Url url: String, @FieldMap map: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @POST
    fun postJson(@Url url: String, @QueryMap common: Map<String, @JvmSuppressWildcards Any>, @Body body: RequestBody): Call<ResponseBody>
}