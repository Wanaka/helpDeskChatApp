package com.example.helpdeskchatapp.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.example.helpdeskchatapp.R
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap

object QRCodeGenerator {

    fun generate(
        context: Context,
        content: String,
        size: Int = 1024
    ): Bitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 0
            )
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)

            val avatar = BitmapFactory.decodeResource(context.resources, R.drawable.avatar_qr)
            val scaled = avatar.scale(size, size)

            val bitmap = createBitmap(size, size)
            val canvas = Canvas(bitmap)

            canvas.drawBitmap(scaled, 0f, 0f, null)

            val paint = Paint().apply { isAntiAlias = false }
            for (x in 0 until size) {
                for (y in 0 until size) {
                    if (bitMatrix.get(x, y)) {
                        paint.color = Color.WHITE
                        canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                    }
                }
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
