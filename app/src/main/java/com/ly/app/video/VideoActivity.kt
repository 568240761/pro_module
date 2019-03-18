package com.ly.app.video

import android.net.Uri
import android.os.Bundle
import com.ly.pub.PubActivity
import com.ly.pub.util.LogUtil_d
import kotlinx.android.synthetic.main.activity_video.*

const val BUNDLE_VIDEO_PATH = "video_path"

class VideoActivity : PubActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initView()
    }

    private fun initView() {
        player.goBack?.setOnClickListener {
            clickBack()
        }

        val path = intent.getStringExtra(BUNDLE_VIDEO_PATH)
        if (path != null) {
            LogUtil_d(this.javaClass.simpleName, "应用内跳转过来path=$path")
            val uri = Uri.parse(path)
            LogUtil_d(this.javaClass.simpleName, "应用内跳转过来uri=$uri")
            player.setVideoURI(uri, isDebug = BuildConfig.DEBUG)
        } else {
            val uri = intent.data
            if (uri != null) {
                LogUtil_d(this.javaClass.simpleName, "应用外跳转过来uri=$uri")
                player.setVideoURI(uri, isDebug = BuildConfig.DEBUG)
            }
        }
    }
}
