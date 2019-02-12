package com.ly.pub.util

import android.widget.Toast
import androidx.annotation.StringRes
import com.ly.pub.PUBLIC_APPLICATION

/**
 * Created by LanYang on 2018/8/8
 */

fun showToast(msg: String) {
    Toast.makeText(PUBLIC_APPLICATION, msg, Toast.LENGTH_LONG).show()
}

fun showToast(@StringRes id: Int) {
    Toast.makeText(PUBLIC_APPLICATION, id, Toast.LENGTH_LONG).show()
}