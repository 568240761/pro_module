package com.ly.pub

/**
 * Created by LanYang on 2018/8/7
 * 上传文件或下载文件的监听接口
 */
interface PubNetworkProgressListener {

    /**
     * 下载进度回调
     * @param currentProgress 进度值
     */
    fun onProgress(currentProgress: Int)

    /**成功回调*/
    fun onSuccess()

    /**失败回调*/
    fun onFail()
}