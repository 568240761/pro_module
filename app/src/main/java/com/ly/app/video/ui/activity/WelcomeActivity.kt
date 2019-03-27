package com.ly.app.video.ui.activity

import android.os.Bundle
import android.os.Handler
import com.ly.app.video.R
import com.ly.app.video.util.findSourceBeanFromUri
import com.ly.pub.PubActivity
import com.ly.pub.util.LogUtil_d
import com.ly.pub.util.jumpNewPageAndFinish

class WelcomeActivity : PubActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Handler().postDelayed({
            val uri = intent.data
            if (uri != null) {
                LogUtil_d(this.javaClass.simpleName, "应用外跳转过来uri=$uri")
                val sourceEntity = findSourceBeanFromUri(this, uri)
                val bundle = Bundle()
                bundle.putParcelable(BUNDLE_VIDEO_ENTITY, sourceEntity)
                jumpNewPageAndFinish(this@WelcomeActivity, VideoActivity::class.java, bundle)
            } else {
                jumpNewPageAndFinish(this@WelcomeActivity, MainActivity::class.java)
            }
        }, 2000)
    }
}
