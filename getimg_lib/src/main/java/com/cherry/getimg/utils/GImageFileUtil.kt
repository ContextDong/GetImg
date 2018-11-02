package com.cherry.getimg.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.cherry.getimg.R
import com.cherry.getimg.exception.GException
import com.cherry.getimg.exception.GExceptionType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.*

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */
object GImageFileUtil {

    private val TAG = GImageFileUtil::class.java.name
    private const val DEFAULT_COMPRESS_CACHE_DIR = "getimage_cache"


    fun getCompressCacheDir(context: Context, file: File): File {
        context.cacheDir?.let { cacheDir ->
            File(cacheDir, DEFAULT_COMPRESS_CACHE_DIR).apply {
                return if (!this.mkdirs() && (!this.exists() || !this.isDirectory)) {
                    file
                } else {
                    File(this, file.name)
                }
            }
        }
        Log.e(TAG, "default compress cache dir is null")
        return file
    }

    fun delete(path: String?) {
        try {
            path?.let {
                val file = File(path)
                if (!file.delete()) {
                    file.deleteOnExit()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * 将bitmap写入到文件
     *
     * @param bitmap
     */
    fun writeToFile(bitmap: Bitmap?, imageUri: Uri) {
        if (bitmap == null) {
            return
        }
        val file = File(imageUri.path)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.use {
            file.writeBytes(it.toByteArray())
            it.flush()
        }
    }

    /**
     * InputStream 转File
     */
    @Throws(GException::class)
    fun inputStreamToFile(inputStream: InputStream, destFile: File?) {
        if (destFile == null) {
            Log.i(TAG, "inputStreamToFile:destFile not be null")
            throw GException(GExceptionType.TYPE_WRITE_FAIL)
        }
        destFile.writeBytes(inputStream.readBytes())
    }

    /**
     * 获取临时文件
     *
     * @param context
     * @param photoUri
     * @return
     */
    @Throws(GException::class)
    fun getTempFile(context: Activity, photoUri: Uri): File {
        val minType = getMimeType(context, photoUri)
        if (!checkMimeType(context, minType)) {
            throw GException(GExceptionType.TYPE_NOT_IMAGE)
        }
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (filesDir != null && !filesDir.exists()) {
            filesDir.mkdirs()
        }
        return File(filesDir, UUID.randomUUID().toString() + "." + minType)
    }

    /**
     * 检查文件类型是否是图片
     *
     * @param minType
     * @return
     */
    fun checkMimeType(context: Context, minType: String): Boolean {
        return TextUtils.isEmpty(minType) or ".jpg|.gif|.png|.bmp|.jpeg|.webp|".contains(minType.toLowerCase()).apply {
            if (!this) {
                Toast.makeText(context, context.resources.getText(R.string.tip_type_not_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    fun getMimeType(context: Activity, uri: Uri): String {
        val extension =
                if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                    var tempExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
                    if (TextUtils.isEmpty(tempExtension)) {
                        tempExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
                    }
                    tempExtension
                } else {
                    var tempExtension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
                    if (TextUtils.isEmpty(tempExtension)) {
                        tempExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
                    }
                    tempExtension
                }

        return if (TextUtils.isEmpty(extension)) {
            getMimeTypeByFileName(GUriParse.getFileWithUri(uri, context)?.name ?: "")
        } else {
            extension!!
        }
    }

    private fun getMimeTypeByFileName(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf("."), fileName.length)
    }
}