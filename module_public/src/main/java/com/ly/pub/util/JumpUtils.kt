package com.ly.pub.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ly.pub.R

/**
 * Created by LanYang on 2018/6/19
 * 控制页面跳转
 */

/**打开新页面，进入页面使用的动画资源*/
var PUBLIC_ACTIVITY_OPEN_ENTER_ANIM = R.anim.pub_activity_right_in

/**打开新页面，退出页面使用的动画资源*/
var PUBLIC_ACTIVITY_OPEN_EXIT_ANIM = R.anim.pub_activity_left_out

/**关闭旧页面，进入页面使用的动画资源*/
var PUBLIC_ACTIVITY_CLOSE_ENTER_ANIM = R.anim.pub_activity_left_in

/**关闭旧页面，退出页面使用的动画资源*/
var PUBLIC_ACTIVITY_CLOSE_EXIT_ANIM = R.anim.pub_activity_right_out

fun <T> jumpNewPageAndFinish(context: Context, clazz: Class<T>, bundle: Bundle = Bundle(), flag: Int = -1) {
    jumpNewPage(context, clazz, bundle, flag)
    finish(context)
}

fun <T> jumpNewPage(context: Context, clazz: Class<T>, bundle: Bundle = Bundle(), flag: Int = -1) {
    if (context is Activity) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        if (flag != -1) intent.flags = flag
        context.startActivity(intent)
        context.overridePendingTransition(PUBLIC_ACTIVITY_OPEN_ENTER_ANIM, PUBLIC_ACTIVITY_OPEN_EXIT_ANIM)
    }
}

fun <T> jumpNewPageForResult(context: Context, clazz: Class<T>, bundle: Bundle = Bundle(), requestCode: Int = 1, flag: Int = -1) {
    if (context is Activity) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        if (flag != -1) intent.flags = flag
        context.startActivityForResult(intent, requestCode)
        context.overridePendingTransition(PUBLIC_ACTIVITY_OPEN_ENTER_ANIM, PUBLIC_ACTIVITY_OPEN_EXIT_ANIM)
    }
}

fun <T> jumpNewPageForResult(fragment: Fragment, clazz: Class<T>, bundle: Bundle = Bundle(), requestCode: Int = 1, flag: Int = -1) {
    val intent = Intent(fragment.context, clazz)
    intent.putExtras(bundle)
    if (flag != -1) intent.flags = flag
    fragment.startActivityForResult(intent, requestCode)
    fragment.activity!!.overridePendingTransition(PUBLIC_ACTIVITY_OPEN_ENTER_ANIM, PUBLIC_ACTIVITY_OPEN_EXIT_ANIM)
}

fun finishAndAnimation(context: Context) {
    if (context is Activity) {
        context.finish()
        context.overridePendingTransition(PUBLIC_ACTIVITY_CLOSE_ENTER_ANIM, PUBLIC_ACTIVITY_CLOSE_EXIT_ANIM)
    }
}

private fun finish(context: Context) {
    if (context is Activity) {
        context.finish()
    }
}

/**
 * 当整个APP是右进左出的效果时，某些特殊页面需要左进右出，就可以调用该方法
 */
fun <T> jumpNewPageFromLeft(context: Context, clazz: Class<T>, bundle: Bundle = Bundle(), flag: Int = -1) {
    if (context is Activity) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        if (flag != -1) intent.flags = flag
        context.startActivity(intent)
        context.overridePendingTransition(R.anim.pub_activity_left_in, R.anim.pub_activity_right_out)
    }
}
