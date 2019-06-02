package com.ly.app.video.ui.activity

import android.Manifest
import android.database.Cursor
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.ly.app.video.R
import com.ly.app.video.loader.SourceEntity
import com.ly.app.video.loader.VideoLoader
import com.ly.app.video.loader.VideoLoaderCallback
import com.ly.app.video.ui.recycler.SpaceLastItemLinearLayoutDecoration
import com.ly.pub.PUBLIC_IMAGE_LOADER
import com.ly.pub.PubActivity
import com.ly.pub.util.PermissionCallback
import com.ly.pub.util.checkPermission
import com.ly.pub.util.jumpNewPage
import com.ly.pub.util.showToast
import com.ly.video.millisecondToHMS
import com.ly.widget.recycler.adapter.CommonAdapter
import com.ly.widget.recycler.base.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : PubActivity(), VideoLoaderCallback {

    private val mLoader = VideoLoader(this, this)
    private val mList = ArrayList<SourceEntity>()
    private val mAdapter: CommonAdapter<SourceEntity>
        get() = object : CommonAdapter<SourceEntity>(this, R.layout.recycler_item_video, mList) {
            override fun convert(holder: ViewHolder?, t: SourceEntity?, position: Int) {
                PUBLIC_IMAGE_LOADER.showImage(
                    this@MainActivity,
                    holder!!.getView(R.id.video_image),
                    t!!.thumbPath,
                    R.drawable.shape_solid_22000000,
                    R.drawable.shape_solid_22000000
                )

                if (t.duration > 0) {
                    holder.setVisible(R.id.video_duration, true)
                    holder.setText(R.id.video_duration, t.duration.millisecondToHMS())
                } else {
                    holder.setVisible(R.id.video_duration, false)
                }

                holder.setText(R.id.video_name, t.getVideoName())

                if (t.type == 0) {
                    holder.setVisible(R.id.video_advertising, false)
                } else {
                    holder.setVisible(R.id.video_advertising, true)
                }

                holder.setItemOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable(BUNDLE_VIDEO_ENTITY, t)
                    jumpNewPage(this@MainActivity, VideoActivity::class.java, bundle)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(SpaceLastItemLinearLayoutDecoration())
        recycler.adapter = mAdapter

        checkPermission(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            object : PermissionCallback {
                override fun onDenied(result: Int) {
                    showToast("未授予读写权限")
                }

                override fun onGranted() {
                    LoaderManager.getInstance(this@MainActivity).initLoader(0, null, mLoader)
                }
            })

        bottom_bar.replaceMenu(R.menu.menu_bottom_bar)
    }

    override fun onLoadFinished(list: List<SourceEntity>) {
        mList.clear()
        mList.addAll(list)
        if (mList.isEmpty()) {

        } else {
            recycler.adapter?.notifyDataSetChanged()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mList.clear()
        recycler.adapter?.notifyDataSetChanged()
    }


    override fun clickBack() {
        super.clickBack()
    }
}
