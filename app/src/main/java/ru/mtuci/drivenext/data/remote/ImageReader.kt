package ru.mtuci.drivenext.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.max
import ru.mtuci.drivenext.domain.model.AppException

/** Читает картинку по Uri и сжимает до разумного размера перед загрузкой в Storage. */
class ImageReader(private val context: Context) {

    fun readJpeg(uriString: String, maxSide: Int = MAX_SIDE, quality: Int = QUALITY): ByteArray {
        val uri = Uri.parse(uriString)
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            ?: throw AppException("Не удалось открыть выбранное фото.")

        val sample = max(1, max(bounds.outWidth, bounds.outHeight) / maxSide)
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: throw AppException("Не удалось прочитать выбранное фото.")

        return ByteArrayOutputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            bitmap.recycle()
            out.toByteArray()
        }
    }

    private companion object {
        const val MAX_SIDE = 1600
        const val QUALITY = 85
    }
}
