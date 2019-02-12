package com.ly.image.glide

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ly.pub.PUBLIC_IMAGE_ERROR
import com.ly.pub.PUBLIC_IMAGE_PLACEHOLDER
import com.ly.pub.PubImageLoader
import com.ly.pub.util.getCurrentActivity

/**
 * Created by LanYang on 2018/8/6
 * 饿汉单例
 */
object GlideImpl : PubImageLoader {
    /**
     * 响应时间
     */
    private val TIME_OUT = 2000

    /**
     * 加载图片
     *
     * @param context   上下文环境
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    override fun showImage(context: Context, imageView: ImageView, any: Any) {
        showImage(context, imageView, any, PUBLIC_IMAGE_PLACEHOLDER, PUBLIC_IMAGE_ERROR)
    }


    /**
     * 加载图片
     *
     * @param context     上下文环境
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    override fun showImage(
        context: Context,
        imageView: ImageView,
        any: Any,
        placeholder: Int,
        error: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getCurrentActivity(context)!!.isDestroyed) return
        val glideRequest = GlideApp.with(context)
        loadImage(glideRequest, imageView, any, placeholder, error)
    }

    /**
     * 加载图片
     *
     * @param activity  传入Activity实例;Activity实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    override fun showImage(activity: Activity, imageView: ImageView, any: Any) {
        showImage(activity, imageView, any, PUBLIC_IMAGE_PLACEHOLDER, PUBLIC_IMAGE_ERROR)
    }

    /**
     * 加载图片
     *
     * @param activity    传入Activity实例;Activity实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    override fun showImage(
        activity: Activity,
        imageView: ImageView,
        any: Any,
        placeholder: Int,
        error: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) return
        val glideRequest = GlideApp.with(activity)
        loadImage(glideRequest, imageView, any, placeholder, error)
    }

    /**
     * 加载图片
     *
     * @param fragment  传入Fragment实例;Fragment实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView 展示图片的ImageView
     * @param any       url、drawable、bitmap、文件等
     */
    override fun showImage(fragment: Fragment, imageView: ImageView, any: Any) {
        showImage(fragment, imageView, any, PUBLIC_IMAGE_PLACEHOLDER, PUBLIC_IMAGE_ERROR)
    }

    /**
     * 加载图片
     *
     * @param fragment    传入Fragment实例;Fragment实例被销毁时，Glide会自动取消加载并回收资源
     * @param imageView   展示图片的ImageView
     * @param any         url、drawable、bitmap、文件等
     * @param placeholder 占位符资源id;为-1时,不设置占位符
     * @param error       错误符资源id;为-1时,不设置错误占位符
     */
    override fun showImage(
        fragment: Fragment,
        imageView: ImageView,
        any: Any,
        placeholder: Int,
        error: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && fragment.activity!!.isDestroyed) return
        val glideRequest = GlideApp.with(fragment)
        loadImage(glideRequest, imageView, any, placeholder, error)
    }

    private fun loadImage(
        glideRequest: GlideRequests,
        imageView: ImageView,
        any: Any,
        placeholder: Int,
        error: Int
    ) {
        var request = glideRequest.load(any)
        if (placeholder != -1) {
            request = request.placeholder(placeholder)
        }
        if (error != -1) {
            request = request.error(error)
        }
        request.timeout(TIME_OUT)
            .into(imageView)

    }

    override fun clearDiskCache(context: Context) {
        GlideApp.get(context).clearDiskCache()
    }
}