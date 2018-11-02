package com.cherry.getimg.utils

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.cherry.getimg.R
import com.cherry.getimg.exception.GException
import com.cherry.getimg.exception.GExceptionType
import com.cherry.getimg.model.CropOptions
import com.cherry.getimg.model.GConstant

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

object CropUtil {


    /**
     * 裁剪图片
     *
     * @param imageUri  要裁剪的图片
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    @Throws(GException::class)
    fun onCrop(activity: Activity, imageUri: Uri, outPutUri: Uri, options: CropOptions) {
        if (!GImageFileUtil.checkMimeType(activity, GImageFileUtil.getMimeType(activity, imageUri))) {
            Toast.makeText(activity, activity.resources.getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show()
            throw GException(GExceptionType.TYPE_NOT_IMAGE)
        }
        activity.startActivityForResult(GIntentUtil.getCropIntent(imageUri, outPutUri, options), GConstant.RC_CROP)
    }

}