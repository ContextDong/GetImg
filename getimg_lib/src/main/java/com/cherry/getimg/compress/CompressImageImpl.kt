package com.cherry.getimg.compress

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.cherry.getimg.model.CompressConfig
import com.cherry.getimg.model.GImage
import com.cherry.getimg.utils.CompressUtil
import java.io.File

/**
 * @author 董棉生(dongmiansheng @ parkingwang.com)
 * @since 18-11-1
 */

internal class CompressImageImpl(private val context: Context,
                                 private val config: CompressConfig,
                                 private val image: GImage?,
                                 private val listener: CompressResultListener) : CompressImage {


    private val sendMsg = { isCompressSuccess: Boolean, imagePath: String?, msg: String? ->

        if (context !is Activity) {
            throw IllegalStateException("context must be Activity!!!")
        }

        context.runOnUiThread {
            image!!.compressPath = imagePath
            if (isCompressSuccess) {
                listener.onCompressSuccess(image)
            } else {
                listener.onCompressFailed(image, msg)
            }
        }
    }

    override fun compress() {
        if (image == null) {
            listener.onCompressFailed(image, " There are pictures of compress is null.")
            return
        }
        if (TextUtils.isEmpty(image.originPath)) {
            return
        }

        val file = File(image.originPath)
        if (!file.exists() || !file.isFile) {
            return
        }

        CompressUtil.compress(context, image.originPath!!, config, sendMsg)

    }

}
