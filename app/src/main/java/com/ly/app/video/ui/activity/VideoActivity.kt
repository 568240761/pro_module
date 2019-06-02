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
        val source = intent.getParcelableExtra<SourceEntity>(BUNDLE_VIDEO_ENTITY)
        if (source != null) {
            val uri = Uri.parse(source.path)
            LogUtil_d(this.javaClass.simpleName, "uri=$uri")

            player.init(
                title = source.getVideoName(),
                uri = Uri.parse(source.path),
                click = View.OnClickListener { clickBack() },
                isDebug =BuildConfig.DEBUG )
        } else {
            showToast(R.string.toast_video_error)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil_d("video", "onDestroy")
    }
}
