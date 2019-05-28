package com.ly.vidoetest.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import tv.danmaku.ijk.media.player.IMediaPlayer

/**
 * Created by LanYang on 2019/3/1
 * 后台播放服务
 */
class VideoPlayerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {

        var mediaPlayer: IMediaPlayer? = null
            set(mp) {
                if (mediaPlayer != null && mediaPlayer !== mp) {
                    if (mediaPlayer!!.isPlaying)
                        mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    field = null
                }
                field = mp
            }

        private fun newIntent(context: Context): Intent {
            return Intent(context, VideoPlayerService::class.java)
        }

        fun intentToStart(context: Context) {
            context.startService(newIntent(context))
        }

        fun intentToStop(context: Context) {
            context.stopService(newIntent(context))
        }
    }
}
