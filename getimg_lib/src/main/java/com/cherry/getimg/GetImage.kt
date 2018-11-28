package com.cherry.getimg

import android.content.Intent
import android.net.Uri
import com.cherry.getimg.compress.CompressListener
import com.cherry.getimg.model.CompressConfig
import com.cherry.getimg.model.CropOptions
import com.cherry.getimg.model.GResult

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-30
 */

interface GetImage {

    /**
     * 从文件中获取图片（不裁剪）
     */
    fun onPickFromDocuments()

    /**
     * 从文件中获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    fun onPickFromDocumentsWithCrop(outPutUri: Uri, options: CropOptions)

    /**
     * 从相册中获取图片（不裁剪）
     */
    fun onPickFromGallery()

    /**
     * 从相册中获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    fun onPickFromGalleryWithCrop(outPutUri: Uri, options: CropOptions)

    /**
     * 从相机获取图片(不裁剪)
     *
     * @param outPutUri 图片保存的路径
     */
    fun onPickFromCapture(outPutUri: Uri)

    /**
     * 从相机获取图片并裁剪
     *
     * @param outPutUri 图片裁剪之后保存的路径
     * @param options   裁剪配置
     */
    fun onPickFromCaptureWithCrop(outPutUri: Uri, options: CropOptions)


    /**
     * 处理拍照或从相册选择的图片或裁剪的结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    /**
     * 启用图片压缩
     *
     * @param config             压缩图片配置
     * @param compressListener 监听压缩回调
     */
    fun onEnableCompress(config: CompressConfig, compressListener: CompressListener? = null)

    /**
     * 是对拍的照片进行旋转角度纠正
     *
     * @param correctImage
     */
    fun correctImage(correctImage: Boolean)


    interface GetResultListener {

        fun getSuccess(result: GResult)

        fun getFail(result: GResult?, msg: String)

        fun getCancel()
    }

}