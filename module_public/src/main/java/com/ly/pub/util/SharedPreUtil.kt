package com.ly.pub.util

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ly.pub.PUBLIC_APPLICATION

/**
 * Created by LanYang on 2018/8/7
 * 简单数据保存工具
 */

lateinit var mSharedPre: SharedPreferences
lateinit var mEditor: SharedPreferences.Editor

fun initSharedPre() {
    mSharedPre = PreferenceManager.getDefaultSharedPreferences(PUBLIC_APPLICATION)
    mEditor = mSharedPre.edit()
}


fun getSharedPreString(key: String, defaultValue: String): String {
    val value = mSharedPre.getString(key, defaultValue)
    return value ?: ""
}

fun putSharedPreString(key: String, value: String) {
    mEditor.putString(key, value).commit()
}

fun getSharedPreLong(key: String, defaultValue: Long): Long {
    return mSharedPre.getLong(key, defaultValue)
}

fun putSharedPreLong(key: String, value: Long) {
    mEditor.putLong(key, value).commit()
}

fun getSharedPreInt(key: String, defaultValue: Int): Int {
    return mSharedPre.getInt(key, defaultValue)
}

fun putSharedPreInt(key: String, value: Int) {
    mEditor.putInt(key, value).commit()
}

fun getSharedPreBoolean(key: String, defaultValue: Boolean): Boolean {
    return mSharedPre.getBoolean(key, defaultValue)
}

fun putSharedPreBoolean(key: String, value: Boolean) {
    mEditor.putBoolean(key, value).commit()
}


