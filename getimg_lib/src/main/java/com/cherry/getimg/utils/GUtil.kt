package com.cherry.getimg.utils

import android.text.TextUtils

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

object GUtil {

    /**
     * 是否裁剪之后返回数据
     **/
    fun isReturnData(): Boolean {
        //对于联想的手机返回数据
        android.os.Build.MANUFACTURER.apply {
            return !TextUtils.isEmpty(this) && this.toLowerCase().contains("lenovo")
        }
        return false
    }

}