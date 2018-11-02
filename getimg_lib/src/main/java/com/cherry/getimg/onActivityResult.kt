package com.cherry.getimg

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import com.cherry.getimg.ActivityResultController.handleCrop
import com.cherry.getimg.ActivityResultController.handleImgFromCapture
import com.cherry.getimg.ActivityResultController.handleImgFromCaptureWithCrop
import com.cherry.getimg.ActivityResultController.handleImgFromDocument
import com.cherry.getimg.ActivityResultController.handleImgFromDocumentWithCrop
import com.cherry.getimg.ActivityResultController.handleImgFromGallery
import com.cherry.getimg.ActivityResultController.handleImgFromGalleryWithCrop
import com.cherry.getimg.compress.CompressImageImpl
import com.cherry.getimg.compress.CompressResultListener
import com.cherry.getimg.exception.GException
import com.cherry.getimg.model.*
import com.cherry.getimg.utils.CropUtil
import com.cherry.getimg.utils.GImageFileUtil
import com.cherry.getimg.utils.GImageRotateUtil
import com.cherry.getimg.utils.GUriParse
import java.io.File

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

internal fun Activity.onActivityForResult(handler: GInnerResultHandler) {
    when (handler.requestCode) {
        GConstant.RC_CROP -> {
            handleCrop(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL -> {
            handleImgFromGallery(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP -> {
            handleImgFromGalleryWithCrop(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL -> {
            handleImgFromDocument(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP -> {
            handleImgFromDocumentWithCrop(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_CAPTURE -> {
            handleImgFromCapture(handler)
        }
        GConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP -> {
            handleImgFromCaptureWithCrop(handler)
        }
        else -> {
        }
    }
}

internal fun String?.gIsEmpty(): Boolean {
    return this == null || length == 0
}


private object ActivityResultController {

    fun handleCrop(handler: GInnerResultHandler) {
        handler.run {

            if (resultCode == Activity.RESULT_OK) {
                try {
                    GImage(GUriParse.getFilePathWithUri(outputUri, activity), fromType).let { image ->
                        image.isCroped = true
                        result(GResult(image), handler)
                    }
                } catch (e: GException) {
                    //裁剪失败，原图继续下一步处理
                    result(GResult(GImage(outputUri?.path ?: "", fromType)), handler, e.exceptionMsg)
                    e.printStackTrace()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //裁剪的照片没有保存
                if (data != null && outputUri != null) {
                    //获取裁剪的结果数据
                    val bitmap = data.getParcelableExtra<Bitmap>("data")
                    //将裁剪的结果写入到文件
                    GImageFileUtil.writeToFile(bitmap, outputUri)
                    GImage(outputUri.path ?: "", fromType).let { image ->
                        image.isCroped = true
                        result(GResult(image), handler)
                    }
                } else {
                    listener.getCancel()
                }
            } else {
                listener.getCancel()
            }
        }
    }

    fun handleImgFromGallery(handler: GInnerResultHandler) {
        handler.run {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    result(GResult(
                            GImage(GUriParse.getFilePathWithUri(data?.data, activity), GImage.FromType.GALLERY)), this)
                } catch (e: GException) {
                    result(GResult(GImage(data?.data, GImage.FromType.GALLERY)), this, e.exceptionMsg)
                    e.printStackTrace()
                }
            } else {
                listener.getCancel()
            }
        }
    }

    /**
     * 从相册选择照片并裁剪
     */
    fun handleImgFromGalleryWithCrop(handler: GInnerResultHandler) {

        handler.run {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    val imgUri = data.data
                    if (imgUri != null && outputUri != null && cropOptions != null) {
                        CropUtil.onCrop(activity, imgUri, outputUri, cropOptions)
                    }
                } catch (e: GException) {
                    //跳转到系统自带裁剪页面失败，未裁剪原图下一步处理
                    result(GResult(GImage(outputUri, GImage.FromType.GALLERY)), handler, e.exceptionMsg)
                    e.printStackTrace()
                }

            } else {
                listener.getCancel()
            }
        }
    }


    fun handleImgFromDocument(handler: GInnerResultHandler) {
        handler.run {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    result(GResult(GImage(GUriParse.getFilePathWithDocumentsUri(data?.data, activity) ?: "",
                            GImage.FromType.DOCUMENT)), this)
                } catch (e: GException) {
                    result(GResult(GImage(data?.data, GImage.FromType.DOCUMENT)), this, e.exceptionMsg)
                    e.printStackTrace()
                }
            } else {
                listener.getCancel()
            }
        }
    }


    fun handleImgFromDocumentWithCrop(handler: GInnerResultHandler) {
        handler.run {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    val imgUri = data.data
                    if (imgUri != null && outputUri != null && cropOptions != null) {
                        CropUtil.onCrop(activity, imgUri, outputUri, cropOptions)
                    }
                } catch (e: GException) {
                    //跳转到系统自带裁剪页面失败，未裁剪原图下一步处理
                    result(GResult(GImage(outputUri, GImage.FromType.DOCUMENT)), handler, e.exceptionMsg)
                    e.printStackTrace()
                }

            } else {
                listener.getCancel()
            }
        }
    }

    fun handleImgFromCapture(handler: GInnerResultHandler) {
        handler.run {
            if (resultCode == Activity.RESULT_OK) {
                if (correctImage) {
                    GImageRotateUtil.correctImage(activity, outputUri)
                }
                try {
                    result(GResult(GImage(GUriParse.getFilePathWithUri(outputUri, activity), fromType)), handler)
                } catch (e: GException) {
                    result(GResult(GImage(outputUri, fromType)), handler, e.exceptionMsg)
                    e.printStackTrace()
                }

            } else {
                listener.getCancel()
            }
        }
    }

    fun handleImgFromCaptureWithCrop(handler: GInnerResultHandler) {
        handler.run {
            if (resultCode == Activity.RESULT_OK) {
                if (correctImage) {
                    GImageRotateUtil.correctImage(activity, captureTempUri)
                }
                try {
                    if (captureTempUri != null && cropOptions != null) {
                        CropUtil.onCrop(activity, captureTempUri,
                                Uri.fromFile(File(GUriParse.parseOwnUri(activity, outputUri))), cropOptions)
                    }
                } catch (e: GException) {
                    result(GResult(GImage(outputUri, fromType)), handler, e.exceptionMsg)
                    e.printStackTrace()
                }

            } else {
                listener.getCancel()
            }
        }
    }

    private fun result(gResult: GResult, innerResult: GInnerResultHandler, exceptionMsg: String? = null) {

        innerResult.run {
            if (compressConfig != null && exceptionMsg.gIsEmpty()) {
                var progressDialog: ProgressDialog? = null
                if (showCompressProgress) {
                    progressDialog = showProgressDialog(activity, activity.resources.getString(R.string.tip_compress))
                }

                //处理压缩
                CompressImageImpl(activity, compressConfig, gResult.image, object : CompressResultListener {
                    override fun onCompressFailed(img: GImage?, msg: String?) {
                        progressDialog?.hide()
                        handleCallback(img, compressConfig, listener, exceptionMsg)
                    }

                    override fun onCompressSuccess(img: GImage) {
                        progressDialog?.hide()
                        handleCallback(img, compressConfig, listener, exceptionMsg)
                    }
                }).compress()
            } else {
                handleCallback(gResult.image, null, listener, exceptionMsg)
            }

        }

    }

    private fun handleCallback(img: GImage?, compressConfig: CompressConfig?,
                               listener: GetImage.GetResultListener, exceptionMsg: String?) {

        if (img != null && compressConfig != null && !compressConfig.isSaveOriginFile) {
            deleteOriginImage(img)
        }
        if (exceptionMsg.gIsEmpty()) {
            listener.getSuccess(GResult(img!!))
        } else {
            listener.getFail(if (img == null) null else GResult(img), exceptionMsg!!)
        }

    }

    private fun deleteOriginImage(img: GImage) {
        if (img.fromType == GImage.FromType.CAMERA) {
            GImageFileUtil.delete(img.originPath)
            img.originPath = ""
        }
    }

    fun showProgressDialog(activity: Activity?, progressTitle: String?): ProgressDialog? {
        if (activity == null || activity.isFinishing) {
            return null
        }
        var title = activity.resources.getString(R.string.tip_tips)
        if (progressTitle.gIsEmpty()) {
            title = progressTitle!!
        }
        return ProgressDialog(activity).apply {
            setTitle(title)
            setCancelable(false)
            show()
        }
    }

}

