package com.cherry.getimg.compress

import com.cherry.getimg.model.GImage

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-11-28
 */

interface CompressListener {

    fun onStartCompress(img: GImage?)

    fun onCompressSuccess(img: GImage)

    fun onCompressFailed(img: GImage?, msg: String?)
}