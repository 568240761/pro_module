package com.ly.video.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ly.video.render.IRenderView
import com.ly.video.render.TextureRenderView

/**
 * Created by LanYang on 2019/5/29
 */
abstract class AbstractVideoView : FrameLayout, DefaultLifecycleObserver {

    constructor(context: Context) : super(context) {
        loadView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        loadView()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    /**画面渲染*/
    private lateinit var mRender: IRenderView

    private fun loadView() {
        val textureRenderView = TextureRenderView(context)
        textureRenderView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        addView(textureRenderView)
        mRender = textureRenderView

        LayoutInflater.from(context).inflate(getLayoutId(), this, true)

        initView()

        //context必须为[AppCompatActivity]的实例,[AbstractVideoView]才能响应到[AppCompatActivity]的生命周期
        if (context is LifecycleOwner)
            (context as LifecycleOwner).lifecycle.addObserver(this)
    }
}