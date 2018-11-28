package com.cherry.getimg.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import com.cherry.getimg.R
import com.cherry.getimg.exception.GException
import com.cherry.getimg.exception.GExceptionType

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

object GUtil {

    /**
     * 是否裁剪之后返回数据
     **/
    fun isReturnData(): Boolean {
        //对于联想的手机返回数据
        android.os.Build.MANUFACTURER.apply {
            return !TextUtils.isEmpty(this) && this.toLowerCase().contains("lenovo")
        }
        return false
    }

    /**
     * 拍照前检查相机是否可用
     */
    @Throws(GException::class)
    fun captureBySafely(activity: Activity, outPutUri: Uri, requestCode: Int) {
        val intent = GIntentUtil.getCaptureIntent(outPutUri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = activity.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            if (result.isEmpty()) {
                Toast.makeText(activity, activity.resources.getText(R.string.tip_enable_camera), Toast.LENGTH_SHORT).show()
                throw GException(GExceptionType.TYPE_ENABLE_CAMERA)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
        } else {
            var camera: Camera? = null
            try {
                camera = Camera.open()
            } catch (e: Exception) {
            }
            if (camera != null) {
                camera.release()
                activity.startActivityForResult(intent, requestCode)
            } else {
                Toast.makeText(activity, activity.resources.getText(R.string.tip_enable_camera), Toast.LENGTH_SHORT).show()
                throw GException(GExceptionType.TYPE_ENABLE_CAMERA)
            }

        }

    }

}