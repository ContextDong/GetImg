package com.cherry.getimg.utils

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.cherry.getimg.model.CropOptions

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

object GIntentUtil {

    private val TAG = this::class.java.name

    /**
     * 获取裁剪照片的Intent
     *
     * @param targetUri 要裁剪的照片
     * @param outPutUri 裁剪完成的照片
     * @param options   裁剪配置
     * @return
     */
    fun getCropIntent(targetUri: Uri, outPutUri: Uri, options: CropOptions): Intent {
        val isReturnData = GUtil.isReturnData()
        Log.w(TAG, "getCaptureIntentWithCrop:isReturnData:" + if (isReturnData) "true" else "false")
        val intent = Intent("com.android.camera.action.CROP")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(targetUri, "image/*")
        intent.putExtra("crop", "true")
        if (options.aspectX * options.aspectY > 0) {
            intent.putExtra("aspectX", options.aspectX)
            intent.putExtra("aspectY", options.aspectY)
        }
        if (options.outputX * options.outputY > 0) {
            intent.putExtra("outputX", options.outputX)
            intent.putExtra("outputY", options.outputY)
        }
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)
        intent.putExtra("return-data", isReturnData)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true) // no face detection
        return intent
    }

    /**
     * 获取拍照的Intent
     *
     * @return
     */
    fun getCaptureIntent(outPutUri: Uri): Intent {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri)//将拍取的照片保存到指定URI
        return intent
    }

    /**
     * 获取选择照片的Intent
     *
     * @return
     */
    fun getPickIntentWithGallery(): Intent {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK//Pick an item from the data
        intent.type = "image/*"//从所有图片中进行选择
        return intent
    }

    /**
     * 获取从文件中选择照片的Intent
     *
     * @return
     */
    fun getPickIntentWithDocuments(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        return intent
    }
}