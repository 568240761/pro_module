package com.ly.widget.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ly.pub.util.retDip2px

/**
 * Created by LanYang on 2018/9/11
 * 当RecyclerView的LayoutManager为LinearLayoutManager,且滚动方向为水平,Item之间需要空白间距时，可以使用该类
 */
class SpaceLinearLayoutDecoration(
        /**间距*/
        val space: Float = 10f,
        /**第一个item左侧是否需要间距,默认需要*/
        private val isFirstLeftSpace: Boolean = true,
        /**最后个item右侧是否需要间距,默认需要*/
        private val isLastRightSpace: Boolean = true,
        /**item顶部是否需要间距，默认需要*/
        private val isTopSpace: Boolean = true,
        /**item底部是否需要间距，默认需要*/
        private val isBottomSpace: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter!!.itemCount

        val value = retDip2px(space)
        if (isFirstLeftSpace && position == 0) outRect.left = value
        if (isLastRightSpace || position + 1 != count) outRect.right = value
        if (isBottomSpace) outRect.bottom = value
        if (isTopSpace) outRect.top = value
    }
}