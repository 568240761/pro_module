package com.ly.app.video.ui.activity

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.ly.app.video.BuildConfig
import com.ly.app.video.R
import com.ly.app.video.loader.SourceEntity
import com.ly.pub.PubActivity
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.showToast
import kotlinx.android.synthetic.main.activity_video.*

const val BUNDLE_VIDEO_ENTITY = "video_entity"

class VideoActivity : PubActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
        }
        initView()
    }

    private fun initView() {
        player.setGoBackEvent(View.OnClickListener { clickBack() })
        val source = intent.getParcelableExtra<SourceEntity>(BUNDLE_VIDEO_ENTITY)
        if (source != null) {
            player.initViewData(title = source.getVideoName())

            val uri = Uri.parse(source.path)
            LogUtil_d(this.javaClass.simpleName, "uri=$uri")
            player.initData(uri, isDebug = BuildConfig.DEBUG)
        } else {
            showToast(R.string.toast_video_error)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil_d("video","onDestroy")
    }
}
