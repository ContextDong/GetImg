package com.cherry.getimg.model

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */

internal object GConstant{
    /**
     * request Code 裁剪照片
     */
    const val RC_CROP = 1001
    /**
     * request Code 从相机获取照片并裁剪
     */
    const val RC_PICK_PICTURE_FROM_CAPTURE_CROP = 1002
    /**
     * request Code 从相机获取照片不裁剪
     */
    const val RC_PICK_PICTURE_FROM_CAPTURE = 1003
    /**
     * request Code 从文件中选择照片
     */
    const val RC_PICK_PICTURE_FROM_DOCUMENTS_ORIGINAL = 1004
    /**
     * request Code 从文件中选择照片并裁剪
     */
    const val RC_PICK_PICTURE_FROM_DOCUMENTS_CROP = 1005
    /**
     * request Code 从相册选择照
     */
    const val RC_PICK_PICTURE_FROM_GALLERY_ORIGINAL = 1006
    /**
     * request Code 从相册选择照片并裁剪
     */
    const val RC_PICK_PICTURE_FROM_GALLERY_CROP = 1007
}