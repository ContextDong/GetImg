package com.cherry.getimg.exception

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-30
 */

internal class GException(type: GExceptionType) : Exception() {
    val exceptionMsg: String = type.errorMsg
}