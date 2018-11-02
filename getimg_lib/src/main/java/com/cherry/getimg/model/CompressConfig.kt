package com.cherry.getimg.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-11-1
 */

class CompressConfig private constructor() : Parcelable {

    /**
     * 长或宽不超过的最大像素,单位px
     */
    var maxPixel = 1200
        private set
    /**
     * 压缩到的最大大小，单位B
     */
    var maxSize = 100 * 1024
        private set

    /**
     * 是否启用像素压缩
     */
    var enablePixelCompress = true
        private set
    /**
     * 是否启用质量压缩
     */
    var enableQualityCompress = true
        private set

    /**
     * 是否保留原文件
     */
    var isSaveOriginFile = true
        private set

    constructor(parcel: Parcel) : this() {
        maxPixel = parcel.readInt()
        maxSize = parcel.readInt()
        enablePixelCompress = parcel.readByte() != 0.toByte()
        enableQualityCompress = parcel.readByte() != 0.toByte()
        isSaveOriginFile = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(maxPixel)
        parcel.writeInt(maxSize)
        parcel.writeByte(if (enablePixelCompress) 1 else 0)
        parcel.writeByte(if (enableQualityCompress) 1 else 0)
        parcel.writeByte(if (isSaveOriginFile) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CompressConfig> {
        override fun createFromParcel(parcel: Parcel): CompressConfig {
            return CompressConfig(parcel)
        }

        override fun newArray(size: Int): Array<CompressConfig?> {
            return arrayOfNulls(size)
        }
    }

    class Builder {

        private val compressConfig by lazy {
            CompressConfig()
        }

        fun setMaxPixel(maxPixel: Int): Builder {
            compressConfig.maxPixel = maxPixel
            return this
        }

        fun setMaxSize(maxSize: Int): Builder {
            compressConfig.maxSize = maxSize
            return this
        }

        fun setEnablePixelCompress(enablePixelCompress: Boolean): Builder {
            compressConfig.enablePixelCompress = enablePixelCompress
            return this
        }

        fun setEnableQualityCompress(enableQualityCompress: Boolean): Builder {
            compressConfig.enableQualityCompress = enableQualityCompress
            return this
        }

        fun isSaveOriginFile(isSaveOriginFile: Boolean): Builder {
            compressConfig.isSaveOriginFile = isSaveOriginFile
            return this
        }

        fun create() = compressConfig

    }


}