package com.cherry.getimg

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.Fragment
import com.cherry.getimg.compress.CompressListener
import com.cherry.getimg.model.*
import com.cherry.getimg.utils.GIntentUtil
import com.cherry.getimg.utils.GUriParse
import com.cherry.getimg.utils.GUtil

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-30
 */

class GetImgImpl : GetImage {

    private val activity: Activity?
    private var captureTempUri: Uri? = null
    private var outputUri: Uri? = null
    private var cropOptions: CropOptions? = null
    private var compressConfig: CompressConfig? = null
    private var compressListener: CompressListener? = null
    private var correctImage: Boolean = false
    private var fromType = GImage.FromType.GALLERY
    private var listener: GetImage.GetResultListener

    constructor(activity: Activity, listener: GetImage.GetResultListener) {
        this.activity = activity
        this.listener = listener
    }

    constructor(fragment: Fragment, listener: GetImage.GetResultListener) {
        this.activity = fragment.activity
        this.listener = listener
    }

    override fun onPickFromDocuments() {
        selectImage(GImage.FromType.DOCUMENT, false)
    }

    override fun onPickFromDocumentsWithCrop(outPutUri: Uri, options: CropOptions) {
        this.outputUri = outPutUri
        this.cropOptions = options
        selectImage(GImage.FromType.DOCUMENT, true)
    }

    override fun onPickFromGallery() {
        selectImage(GImage.FromType.GALLERY, false)
    }

    override fun onPickFromGalleryWithCrop(outPutUri: Uri, options: CropOptions) {
        this.outputUri = outPutUri
        this.cropOptions = options
        selectImage(GImage.FromType.GALLERY, true)
    }

    private fun selectImage(fromType: GImage.FromType, isCrop: Boolean) {
        this.fromType = fromType
        val intent = when (fromType) {
            GImage.FromType.GALLERY -> GIntentUtil.getPickIntentWithGallery()
            GImage.FromType.DOCUMENT -> GIntentUtil.getPickIntentWithDocuments()
            else -> null
        }

        val requestCode = when (fromType) {
            GImage.FromType.GALLERY -> {
                if (isCrop) GConstant.RC_PICK_PICTURE_FROM_GALLERY_CROP else GConstant.RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL
            }
            GImage.FromType.DOCUMENT -> {
                if (isCrop) GConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_CROP else GConstant.RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL
            }
            else -> -1
        }

        if (intent == null || requestCode == -1) {
            return
        }
        activity?.startActivityForResult(intent, requestCode)
    }

    override fun onPickFromCapture(outPutUri: Uri) {
        this.fromType = GImage.FromType.CAMERA

        this.outputUri = if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GUriParse.convertFileUriToFileProviderUri(activity, outPutUri)
        } else {
            outputUri
        }

        if (activity != null && this.outputUri != null) {
            try {
                GUtil.captureBySafely(activity, this.outputUri!!, GConstant.RC_PICK_PICTURE_FROM_CAPTURE)
            } catch (e: Exception) {
                listener.getFail(null, activity.resources.getString(R.string.tip_enable_camera))
            }
        }
    }

    override fun onPickFromCaptureWithCrop(outPutUri: Uri, options: CropOptions) {
        this.fromType = GImage.FromType.CAMERA
        this.outputUri = if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GUriParse.convertFileUriToFileProviderUri(activity, outPutUri)
        } else {
            outPutUri
        }

        this.captureTempUri = this.outputUri
        this.cropOptions = options
        if (activity != null && this.outputUri != null) {
            GUtil.captureBySafely(activity, this.outputUri!!, GConstant.RC_PICK_PICTURE_FROM_CAPTURE_CROP)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.onActivityForResult(GInnerResultHandler(activity, requestCode, resultCode, data, captureTempUri,
                outputUri, cropOptions, compressConfig, fromType, compressListener, correctImage, listener))
    }

    override fun onEnableCompress(config: CompressConfig, compressListener: CompressListener?) {
        this.compressConfig = config
        this.compressListener = compressListener
    }

    override fun correctImage(correctImage: Boolean) {
        this.correctImage = correctImage
    }


}