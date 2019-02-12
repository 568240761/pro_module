package com.ly.pub.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * Created by LanYang on 2018/7/13
 * 动态权限管理
 */
fun checkPermission(context: Context, permissions: Array<String>, callback: PermissionCallback) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        callback.onGranted()
        return
    }

    val list = ArrayList<String>()
    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            list.add(permission)
        }
    }
    if (list.size > 0) {
        PermissionActivity.requestPermission(context, list, callback)
    } else {
        callback.onGranted()
    }
}

private const val INTENT_EXTRA_KEY_PERMISSION = "KEY_PERMISSION"

class PermissionActivity : AppCompatActivity() {

    companion object {
        private var callback: PermissionCallback? = null
        fun requestPermission(context: Context, permissions: ArrayList<String>, permissionCallback: PermissionCallback) {
            callback = permissionCallback
            val intent = Intent(context, PermissionActivity::class.java)
            intent.putExtra(INTENT_EXTRA_KEY_PERMISSION, permissions)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
    private val REQUEST_CODE = 11
    private lateinit var permissions: ArrayList<String>
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置状态栏颜色
        window.statusBarColor = Color.TRANSPARENT
        permissions = intent.getStringArrayListExtra(INTENT_EXTRA_KEY_PERMISSION)

        val array = Array<String>(permissions.size) {
            permissions.get(it)
        }
        requestPermissions(array, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            var flag = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    callback?.onDenied(result)
                    flag = false
                    break
                }
            }
            if (flag) {
                callback?.onGranted()
            }

        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        //该回调实例是静态的,很容易内存泄漏，所以置空
        callback = null
    }
}

open class PermissionCallback {
    /**
     * 同意授权回调
     */
    open fun onGranted() {}

    /**
     * 拒绝授权回调
     */
    open fun onDenied(result: Int) {

    }
}
