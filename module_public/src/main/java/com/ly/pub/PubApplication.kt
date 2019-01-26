package com.ly.pub

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.ly.pub.util.initSharedPre

/**
 * Created by LanYang on 2018/8/6
 */
abstract class PubApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        PUBLIC_APPLICATION = this
        if (PUBLIC_IS_MULTI_DEX) {
            MultiDex.install(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initSharedPre()
    }

}