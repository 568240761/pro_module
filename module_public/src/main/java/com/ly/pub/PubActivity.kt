package com.aiqin.pub

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.ly.pub.util.*

/**
 * Created by LanYang on 2018/8/6
 */
abstract class PubActivity : AppCompatActivity(), Receiver {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addActivity(this)
        registerReceivers(this)
    }

    override fun onBackPressed() {
        clickBack()
    }

    open fun clickBack() {
        if (getActivitySize() > 1) {
            finishAndAnimation(this)
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivity(this)
        removeReceivers(this)
    }


    /**
     * 状态栏字体高亮,即为黑色
     * @param color 设置状态栏颜色;低于6.0高于5.0,改变状态栏颜色
     */
    fun changeLightBar(@ColorInt color: Int = Color.BLACK) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                //设置状态栏颜色
                window.statusBarColor = color
            }

        }
    }

    /**
     * 将状态栏改变为透明颜色
     * @param flag 是否设置状态栏字体高亮,即为黑色
     */
    fun changeTransparent(flag: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && flag) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
            //设置状态栏颜色
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    /**全屏*/
    fun changeFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    protected var IS_AUTO_HIDE_SOFT_INPUT = true
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (IS_AUTO_HIDE_SOFT_INPUT && ev!!.action == MotionEvent.ACTION_DOWN) {
            //点击软键盘以外的区域,收起输入法软键盘
            val view = currentFocus
            if (isShouldHideInput(view, ev)) {
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }
}