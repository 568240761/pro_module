package com.ly.widget.recycler.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ly.pub.util.retDip2px

/**
 * Created by LanYang on 2018/8/25
 * 当RecyclerView的LayoutManager为GridLayoutManager，Item之间需要空白间距时，可以使用该类
 */
class SpaceGridLayoutDecoration(
        /**间距*/
        val space: Float = 10f,
        /**item左侧是否需要间距,默认需要*/
        private val isLeftSpace: Boolean = true,
        /**item右侧是否需要间距,默认需要*/
        private val isRightSpace: Boolean = true,
        /**第一行item顶部是否需要间距，默认需要*/
        private val isFirstTopSpace: Boolean = true,
        /**最后一行item底部是否需要间距，默认需要*/
        private val isLastBottomSpace: Boolean = true
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager as GridLayoutManager

        if (layoutManager.spanCount > position && isFirstTopSpace) {
            outRect.top = retDip2px(space)
        }
        if (isLastBottomSpace || position != parent.adapter!!.itemCount - 1) {
            outRect.bottom = retDip2px(space)
        }

        when {
            layoutManager.spanCount == 1 -> {
                if (isLeftSpace) {
                    outRect.left = retDip2px(space)
                }
                if (isRightSpace) {
                    outRect.right = retDip2px(space)
                }
            }
            position % layoutManager.spanCount == 0 -> {
                if (isLeftSpace) {
                    outRect.left = retDip2px(space)
                }
                if (isRightSpace) {
                    outRect.right = retDip2px(space / 2)
                }
            }
            position % layoutManager.spanCount == 1 -> {
                if (isRightSpace) {
                    outRect.right = retDip2px(space)
                }
                if (isLeftSpace) {
                    outRect.left = retDip2px(space / 2)
                }
            }
            else -> {
                if (isRightSpace) {
                    outRect.right = retDip2px(space / 2)
                }
                if (isLeftSpace) {
                    outRect.left = retDip2px(space / 2)
                }
            }
        }
    }

}