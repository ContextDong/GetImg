package com.cherry.getimg.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */
class GImage : Parcelable {


    var originPath: String? = null
    var compressPath: String? = null
    var isCroped: Boolean = false
    var isCompressed: Boolean = false
    var fromType: FromType = FromType.GALLERY

    constructor(originPath: String, fromType: FromType) {
        this.originPath = originPath
        this.fromType = fromType
    }

    constructor(originUri: Uri?, fromType: FromType) {
        this.originPath = originUri?.path
        this.fromType = fromType
    }

    constructor(parcel: Parcel) {
        parcel.readString()
        parcel.readString()
        parcel.readByte() != 0.toByte()
        parcel.readByte() != 0.toByte()
        parcel.readParcelable<FromType>(FromType::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(originPath)
        parcel.writeString(compressPath)
        parcel.writeByte(if (isCroped) 1 else 0)
        parcel.writeByte(if (isCompressed) 1 else 0)
        parcel.writeParcelable(fromType, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GImage> {
        override fun createFromParcel(parcel: Parcel): GImage {
            return GImage(parcel)
        }

        override fun newArray(size: Int): Array<GImage?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 枚举实现Parcelable
     */
    enum class FromType : Parcelable {

        CAMERA,
        DOCUMENT,
        GALLERY;

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(ordinal)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FromType> {
            override fun createFromParcel(parcel: Parcel): FromType {
                return FromType.values()[parcel.readInt()]
            }

            override fun newArray(size: Int): Array<FromType?> {
                return arrayOfNulls(size)
            }
        }


    }

}