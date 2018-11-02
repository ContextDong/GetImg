package com.cherry.imgsample

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.cherry.getimg.GetImage
import com.cherry.getimg.GetImgImpl
import com.cherry.getimg.model.CompressConfig
import com.cherry.getimg.model.CropOptions
import com.cherry.getimg.model.GResult
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), GetImage.GetResultListener {

    val tag = this::class.java.name

    val getImage by lazy {
        GetImgImpl(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        RxPermissions(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { grant ->
                    if (grant) {
                        initStartWork()
                    }
                }

    }

    private fun initStartWork() {
        //裁剪后的输出路径
        val file = File(Environment.getExternalStorageDirectory(), "/111111GetImg/"
                + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        getImage.onEnableCompress(getCompressConfig(), true)
        getImage.correctImage(true)

        gallery.setOnClickListener {
            getImage.onPickFromGallery()
        }

        galleryCrop.setOnClickListener {
            getImage.onPickFromGalleryWithCrop(Uri.fromFile(file), getCropOptions())
        }

        document.setOnClickListener {
            getImage.onPickFromDocuments()
        }

        documentCrop.setOnClickListener {
            getImage.onPickFromDocumentsWithCrop(Uri.fromFile(file), getCropOptions())
        }

        capture.setOnClickListener {
            getImage.onPickFromCapture(Uri.fromFile(file))
        }

        captureCrop.setOnClickListener {
            getImage.onPickFromCaptureWithCrop(Uri.fromFile(file), getCropOptions())
        }
    }

    private fun getCompressConfig(): CompressConfig {
        //质量压缩可用在缩略图和应用间的图片分享
        return CompressConfig.Builder()
                .setEnablePixelCompress(true)
//                .setEnableQualityCompress(true)
                .setMaxPixel(1080)
//                .setMaxSize(4 * 1024)
                .create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getImage.onActivityResult(requestCode, resultCode, data)
    }


    private fun getCropOptions(): CropOptions {
        return CropOptions.Builder().setOutputX(400).setOutputY(400).create()
    }

    override fun getSuccess(result: GResult) {
        Log.e(tag, "${if (result.image.isCroped) "裁剪图片路径" else "原图路径"}：${result.image.originPath ?: ""}")
        Log.e(tag, "图片压缩路径：${result.image.compressPath ?: ""}")

        result.image.originPath?.let {
            val bitmap = BitmapFactory.decodeStream(File(it).inputStream())
            Log.e(tag, "原图bitmap大小：${bitmap.byteCount / 1024}k")
            Log.e(tag, "原图片在sd卡中大小：${File(it).length() / 1024}k")
        }

        result.image.compressPath?.let {
            val bitmap = BitmapFactory.decodeStream(File(it).inputStream())
            Log.e(tag, "压缩bitmap大小：${bitmap.byteCount / 1024}k")
            Log.e(tag, "压缩图片在sd卡中大小：${File(it).length() / 1024}k")
            show.setImageBitmap(bitmap)
        }
    }

    override fun getFail(result: GResult?, msg: String) {
        Log.e(tag, result?.image?.originPath ?: "")
        Toast.makeText(applicationContext, "错误信息:$msg", Toast.LENGTH_SHORT).show()
    }

    override fun getCancel() {
        Toast.makeText(applicationContext, "取消了", Toast.LENGTH_SHORT).show()
    }
}

