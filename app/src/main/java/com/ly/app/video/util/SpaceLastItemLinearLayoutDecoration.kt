package com.ly.app.video.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ly.pub.util.retActionBarHeight

/**
 * Created by LanYang on 2019/3/21
 * 当RecyclerView的LayoutManager为LinearLayoutManager,且滚动方向为垂直;
 * 仅设置最后Item距离底部的间距
 */

class SpaceLastItemLinearLayoutDecoration(
    /**最后Item距离底部的间距,默认为ActionBar高度*/
    var lastSpace: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter!!.itemCount

        if (lastSpace == 0) lastSpace = retActionBarHeight()

        if (position + 1 == count) {
            outRect.bottom = lastSpace
        }
    }
}