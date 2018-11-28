package com.cherry.getimg.model

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.cherry.getimg.GetImage
import com.cherry.getimg.compress.CompressListener

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-11-1
 */

internal data class GInnerResultHandler(val activity: Activity,
                                        val requestCode: Int,
                                        val resultCode: Int,
                                        val data: Intent?,
                                        val captureTempUri: Uri?,
                                        val outputUri: Uri?,
                                        val cropOptions: CropOptions?,
                                        val compressConfig: CompressConfig?,
                                        val fromType: GImage.FromType,
                                        val compressListener: CompressListener?,
                                        val correctImage: Boolean,
                                        val listener: GetImage.GetResultListener)