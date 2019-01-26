package com.ly.pub

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment

/**
 * Created by LanYang on 2018/8/6
 * 图片加载接口
 */
interface PubImageLoader {
    /**
     * 加载图片
     *
     * @param context   上下文环境
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    fun showImage(context: Context, imageView: ImageView, any: Any)

    /**
     * 加载图片
     *
     * @param context     上下文环境
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    fun showImage(context: Context, imageView: ImageView, any: Any, @DrawableRes placeholder: Int, @DrawableRes error: Int)

    /**
     * 加载图片
     *
     * @param activity  传入Activity实例;Activity实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    fun showImage(activity: Activity, imageView: ImageView, any: Any)

    /**
     * 加载图片
     *
     * @param activity    传入Activity实例;Activity实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    fun showImage(activity: Activity, imageView: ImageView, any: Any, @DrawableRes placeholder: Int, @DrawableRes error: Int)

    /**
     * 加载图片
     *
     * @param fragment  传入Fragment实例;Fragment实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    fun showImage(fragment: Fragment, imageView: ImageView, any: Any)

    /**
     * 加载图片
     *
     * @param fragment    传入Fragment实例;Fragment实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    fun showImage(fragment: Fragment, imageView: ImageView, any: Any, @DrawableRes placeholder: Int, @DrawableRes error: Int)

    /**
     * 清除磁盘上的图片缓存
     */
    fun clearDiskCache(context: Context)
}