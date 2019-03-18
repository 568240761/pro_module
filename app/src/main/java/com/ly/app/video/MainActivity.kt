package com.ly.app.video

import android.Manifest
import android.database.Cursor
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.ly.pub.PUBLIC_IMAGE_LOADER
import com.ly.pub.PubActivity
import com.ly.pub.util.PermissionCallback
import com.ly.pub.util.checkPermission
import com.ly.pub.util.jumpNewPage
import com.ly.pub.util.showToast
import com.ly.widget.recycler.adapter.CommonAdapter
import com.ly.widget.recycler.base.ViewHolder
import com.ly.widget.recycler.decoration.SpaceGridLayoutDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : PubActivity(), VideoLoaderCallback {

    private val mLoader = VideoLoader(this, this)
    private val mList = ArrayList<VideoEntity>()
    private val mAdapter: CommonAdapter<VideoEntity>
        get() = object : CommonAdapter<VideoEntity>(this, R.layout.recycler_item_video, PUBLIC_IMAGE_LOADER, mList) {
            override fun convert(holder: ViewHolder?, t: VideoEntity?, position: Int) {
                holder!!.initImageData(R.id.video_image, t!!.thumbPath,R.drawable.shape_recycler_item_video_bg)
                holder.setItemOnClickListener {
                    val bundle = Bundle()
                    bundle.putString(BUNDLE_VIDEO_PATH,t.path)
                    jumpNewPage(this@MainActivity,VideoActivity::class.java,bundle)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.addItemDecoration(SpaceGridLayoutDecoration(space = 16f))
        recycler.adapter = mAdapter

        checkPermission(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            object : PermissionCallback() {
                override fun onDenied(result: Int) {
                    super.onDenied(result)
                    showToast("未授予读写权限")
                    clickBack()
                }

                override fun onGranted() {
                    super.onGranted()
                    LoaderManager.getInstance(this@MainActivity).initLoader(0, null, mLoader)
                }
            })
    }

    override fun onLoadFinished(list: List<VideoEntity>) {
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
}
