package com.cherry.getimg.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.cherry.getimg.model.CompressConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * @author 董棉生(dongmiansheng@parkingwang.com)
 * @since 18-11-1
 */

typealias SendMsg = (isCompressSuccess: Boolean, imgPath: String?, msg: String?) -> Unit

object CompressUtil {

    private val singleWorkThread by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun compress(context: Context, imagePath: String, config: CompressConfig, sendMsg: SendMsg) {
        if (config.enablePixelCompress) {
            try {
                compressImageByPixel(context, imagePath, config, sendMsg)
            } catch (e: FileNotFoundException) {
                sendMsg(false, imagePath, String.format("图片压缩失败,%s"))
                e.printStackTrace()
            }

        } else {
            compressImageByQuality(context, BitmapFactory.decodeFile(imagePath), imagePath, config, sendMsg)
        }
    }


    /**
     * 采样率压缩（像素压缩） 改变Bitmap内存大小
     * 按比例缩小图片的像素以达到压缩的目的
     *
     * @param imgPath
     * @return
     */
    @Throws(FileNotFoundException::class)
    private fun compressImageByPixel(context: Context, imgPath: String?,
                                     config: CompressConfig, sendMsg: SendMsg) {
        if (imgPath == null) {
            sendMsg(false, imgPath, "要压缩的文件不存在")
            return
        }
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = true//只读边,不读内容
        BitmapFactory.decodeFile(imgPath, newOpts)
        newOpts.inJustDecodeBounds = false
        val width = newOpts.outWidth
        val height = newOpts.outHeight
        val maxSize = config.maxPixel
        var be = 1
        if (width >= height && width > maxSize) {//缩放比,用高或者宽其中较大的一个数据进行计算
            be = (newOpts.outWidth / maxSize)
            be++
        } else if (width < height && height > maxSize) {
            be = (newOpts.outHeight / maxSize)
            be++
        }
        newOpts.inSampleSize = be//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888//该模式是默认的,可不设
        newOpts.inPurgeable = true// 同时设置才会有效
        newOpts.inInputShareable = true//当系统内存不够时候图片自动被回收
        val bitmap = BitmapFactory.decodeFile(imgPath, newOpts)
        if (config.enableQualityCompress) {
            compressImageByQuality(context, bitmap, imgPath, config, sendMsg)//压缩好比例大小后再进行质量压缩
        } else {
            val thumbnailFile = getThumbnailFile(context, File(imgPath))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(thumbnailFile))
            sendMsg(true, thumbnailFile?.path, "压缩成功")
        }
    }

    /**
     * 质量压缩，改变图片在磁盘的文件大小
     * 多线程压缩图片的质量
     *
     * @param bitmap  内存中的图片
     * @param imgPath 图片的保存路径
     */
    private fun compressImageByQuality(context: Context, bitmap: Bitmap?, imgPath: String,
                                       config: CompressConfig, sendMsg: SendMsg) {
        if (bitmap == null) {
            sendMsg(false, imgPath, "像素压缩失败,bitmap is null")
            return
        }
        val task = Runnable {
            val baos = ByteArrayOutputStream()
            var options = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
            while (baos.toByteArray().size > config.maxSize) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
                baos.reset()//重置baos即让下一次的写入覆盖之前的内容
                options -= 5//图片质量每次减少5
                if (options <= 5) {
                    options = 5//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)//将压缩后的图片保存到baos中
                if (options == 5) {
                    break//如果图片的质量已降到最低则，不再进行压缩
                }
            }


            val thumbnailFile = getThumbnailFile(context, File(imgPath))
            //将压缩后的图片保存的本地上指定路径中
            if (thumbnailFile != null) {
                baos.use {
                    thumbnailFile.writeBytes(it.toByteArray())
                    it.flush()
                    sendMsg(true, thumbnailFile.path, null)
                }
            } else {
                sendMsg(false, imgPath, "质量压缩失败,thumbnailFile is null")
            }
        }

        singleWorkThread.submit(task)

    }

    private fun getThumbnailFile(context: Context, file: File?): File? {
        return if (file == null || !file.exists()) {
            file
        } else GImageFileUtil.getCompressCacheDir(context, file)
    }


}