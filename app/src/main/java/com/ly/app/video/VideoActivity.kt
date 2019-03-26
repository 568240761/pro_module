package com.ly.app.video

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import com.ly.pub.PubActivity
import com.ly.pub.util.LogUtil_d
import com.ly.video.millisecondToHMS
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

    @SuppressLint("SetTextI18n")
    private fun initData(source: SourceEntity) {
        player.title.text = source.getVideoName()
        player.duration.text = millisecondToHMS(source.duration)
        player.current.text = "0:00"
    }
}
