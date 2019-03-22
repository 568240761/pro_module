package com.ly.widget.recycler.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.ly.pub.util.retDip2px
import com.ly.widget.R

/**
 * Created by LanYang on 2018/8/23
 * 当RecyclerView的LayoutManager为LinearLayoutManager，Item之间需要画线时，可以使用该类
 */
class LineLinearLayoutDecoration(
    /**线的高度*/
    val lineHeight: Float = 1f,
    /**Line距离左边的间隔*/
    val leftMargin: Float = 0f,
    /**Line距离右边的间隔*/
    val rightMargin: Float = 0f,
    /**是否绘制最后一个item的Line,默认为false;*/
    val isDrawBottomLine: Boolean = false,
    /**Line的颜色*/
    @ColorRes val color: Int = R.color.widget_recycler_decoration_line_color
) : RecyclerView.ItemDecoration() {
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var context: Context
    private val height = retDip2px(lineHeight)

    init {
        paint.style = Paint.Style.FILL
    }

    @Suppress("DEPRECATION")
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (!this::context.isInitialized) {
            context = parent.context
            paint.color = context.resources.getColor(color)
        }
        val left = parent.paddingLeft + retDip2px(leftMargin)
        val right = parent.measuredWidth - parent.paddingRight - retDip2px(rightMargin)
        val childSize = parent.childCount
        var i = 0
        while (i < childSize) {
            if (!isDrawBottomLine && childSize - i == 1) break
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + layoutParams.bottomMargin
            val bottom = top + height
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            i++
        }
    }
}
