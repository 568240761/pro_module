package com.ly.app.video.ui.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
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
        initView()
    }

    private fun initView() {
        player.goBack.setOnClickListener {
            clickBack()
        }

        val source = intent.getParcelableExtra<SourceEntity>(BUNDLE_VIDEO_ENTITY)
        if (source != null) {
            initData(source)
            val uri = Uri.parse(source.path)
            LogUtil_d(this.javaClass.simpleName, "uri=$uri")
            player.setVideoURI(uri, isDebug = BuildConfig.DEBUG)
        } else {
            showToast(R.string.toast_video_error)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData(source: SourceEntity) {
        player.title.text = source.getVideoName()
    }
}
