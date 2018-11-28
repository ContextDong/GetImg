package com.cherry.getimg.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import com.cherry.getimg.exception.GException
import com.cherry.getimg.exception.GExceptionType
import com.cherry.getimg.model.GConstant
import java.io.File
import java.io.FileNotFoundException


/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-10-31
 */
object GUriParse {

    private val TAG = GUriParse::class.java.name


    /**
     * 将scheme为file的uri转成FileProvider 提供的content uri
     *
     * @param context
     * @param uri
     * @return
     */
    fun convertFileUriToFileProviderUri(context: Context, uri: Uri?): Uri? {
        if (uri == null) {
            return null
        }
        return if (ContentResolver.SCHEME_FILE == uri.scheme) {
            getUriForFile(context, File(uri.path))
        } else uri

    }

    private fun getUriForFile(context: Context, file: File) = FileProvider.getUriForFile(context, GConstant.getFileProviderName(context), file)

    /**
     * 通过URI获取文件
     *
     * @param uri
     * @param activity
     */
    fun getFileWithUri(uri: Uri, activity: Activity): File? {
        var picturePath: String? = null
        val scheme = uri.scheme
        if (ContentResolver.SCHEME_CONTENT == scheme) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.contentResolver.query(uri, filePathColumn, null, null, null)//从系统表中查询指定Uri对应的照片
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            if (columnIndex >= 0) {
                picturePath = cursor.getString(columnIndex)  //获取照片路径
            } else if (TextUtils.equals(uri.authority, GConstant.getFileProviderName(activity))) {
                picturePath = parseOwnUri(activity, uri)
            }
            cursor.close()
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            picturePath = uri.path
        }
        return if (TextUtils.isEmpty(picturePath)) null else File(picturePath!!)
    }


    /**
     * 通过URI获取文件的路径
     *
     * @param uri
     * @param activity
     */
    @Throws(GException::class)
    fun getFilePathWithUri(uri: Uri?, activity: Activity): String {
        if (uri == null) {
            Log.w(TAG, "uri is null,activity may have been recovered?")
            throw GException(GExceptionType.TYPE_URI_NULL)
        }
        val picture = getFileWithUri(uri, activity)
        val picturePath = picture?.path
        if (TextUtils.isEmpty(picturePath)) {
            throw GException(GExceptionType.TYPE_URI_PARSE_FAIL)
        }
        if (!GImageFileUtil.checkMimeType(activity, GImageFileUtil.getMimeType(activity, uri))) {
            throw GException(GExceptionType.TYPE_NOT_IMAGE)
        }
        return picturePath!!
    }

    /**
     * 通过从文件中得到的URI获取文件的路径
     *
     * @param uri
     * @param activity
     */
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Throws(GException::class)
    fun getFilePathWithDocumentsUri(uri: Uri?, activity: Activity): String? {
        if (uri == null) {
            Log.e(TAG, "uri is null,activity may have been recovered?")
            return null
        }
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme && uri.path.contains("document")) {
            val tempFile = GImageFileUtil.getTempFile(activity, uri)
            try {
                GImageFileUtil.inputStreamToFile(activity.contentResolver.openInputStream(uri), tempFile)
                tempFile.path
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                throw GException(GExceptionType.TYPE_NO_FIND)
            }

        } else {
            getFilePathWithUri(uri, activity)
        }
    }

    /**
     * Uri转文件的绝对路径
     */
    fun parseOwnUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }

        return if (TextUtils.equals(uri.authority, GConstant.getFileProviderName(context))) {
            File(uri.path?.replace("camera_photos/", "")).absolutePath
        } else {
            uri.path
        }
    }

}
