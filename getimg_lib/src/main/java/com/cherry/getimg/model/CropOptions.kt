package com.cherry.getimg.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-30
 */

class CropOptions : Parcelable {

    /* 以宽/高比例裁剪 */
    var aspectX: Int = 0
        private set
    var aspectY: Int = 0
        private set

    /*以宽*高尺寸裁剪*/
    var outputX: Int = 0
        private set
    var outputY: Int = 0
        private set

    private constructor()

    private constructor(parcel: Parcel) {
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
        parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(aspectX)
        parcel.writeInt(aspectY)
        parcel.writeInt(outputX)
        parcel.writeInt(outputY)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CropOptions> {
        override fun createFromParcel(parcel: Parcel): CropOptions {
            return CropOptions(parcel)
        }

        override fun newArray(size: Int): Array<CropOptions?> {
            return arrayOfNulls(size)
        }
    }

    class Builder {

        private val cropOptions: CropOptions by lazy {
            CropOptions()
        }

        fun setAspectX(aspectX: Int): Builder {
            cropOptions.aspectX = aspectX
            return this
        }

        fun setAspectY(aspectY: Int): Builder {
            cropOptions.aspectY = aspectY
            return this
        }

        fun setOutputX(outputX: Int): Builder {
            cropOptions.outputX = outputX
            return this
        }

        fun setOutputY(outputY: Int): Builder {
            cropOptions.outputY = outputY
            return this
        }

        fun create() = cropOptions
    }


}