package com.cherry.getimg.exception

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-30
 */

internal enum class GExceptionType(val errorMsg: String) {

    TYPE_NOT_IMAGE("选择的文件不是图片"),
    TYPE_WRITE_FAIL("保存选择的的文件失败"),
    TYPE_URI_NULL("所选照片的Uri 为null"),
    TYPE_URI_PARSE_FAIL("从Uri中获取文件路径失败"),
    TYPE_ENABLE_CAMERA("相机不可用"),
    TYPE_NO_FIND("选择的文件没有找到")

}