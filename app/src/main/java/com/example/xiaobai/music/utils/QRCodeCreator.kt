package com.example.xiaobai.music.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*

/**
 *   Create by mzf on 2020/3/21
 *   Describe:  二维码生成
 */
object QRCodeCreator {

    fun createQRCode(content: String,width: Int,height: Int,logo: Bitmap?): Bitmap? {
        if (content.isEmpty()) return null
        var offsetX = width / 2
        var offsetY = height / 2
        var logoBitmap: Bitmap? = null

        if (logo != null) {
            val matrix = Matrix()
            val scaleFactor: Float = Math.min(width * 1.0f / 5 / logo.width, height * 1.0f / 5 / logo.height)
            matrix.postScale(scaleFactor, scaleFactor)
            logoBitmap = Bitmap.createBitmap(logo, 0, 0, logo.width, logo.height, matrix, true)
        }
        /*如果log不为null,重新计算偏移量*/
        /*如果log不为null,重新计算偏移量*/
        var logoW = 0
        var logoH = 0
        if (logoBitmap != null) {
            logoW = logoBitmap.width
            logoH = logoBitmap.height
            offsetX = (width - logoW) / 2
            offsetY = (height - logoH) / 2
        }

        /*指定为UTF-8*/
        /*指定为UTF-8*/
        val hints = Hashtable<EncodeHintType, Any?>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        //容错级别
        //容错级别
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 0
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        val matrix: BitMatrix
        return try {
            matrix = MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, width, height, hints)
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (x >= offsetX && x < offsetX + logoW && y >= offsetY && y < offsetY + logoH) {
                        var pixel = logoBitmap!!.getPixel(x - offsetX, y - offsetY)
                        if (pixel == 0) {
                            pixel = if (matrix[x, y]) {
                                -0x1000000
                            } else {
                                -0x1
                            }
                        }
                        pixels[y * width + x] = pixel
                    } else {
                        if (matrix[x, y]) {
                            pixels[y * width + x] = -0x1000000
                        } else {
                            pixels[y * width + x] = -0x1
                        }
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}