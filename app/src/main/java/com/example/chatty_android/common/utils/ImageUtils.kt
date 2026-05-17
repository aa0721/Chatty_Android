package com.example.chatty_android.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.chatty_android.common.constants.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    private const val MAX_AVATAR_SIZE = 480

    fun compressImage(context: Context, uri: Uri, maxSize: Long = Constants.MAX_IMAGE_SIZE): File? {
        return try {
            val bitmap = decodeSampledBitmap(context, uri, 1024)
                ?: return null
            val correctedBitmap = correctOrientation(context, uri, bitmap)
            val outputDir = File(context.cacheDir, Constants.IMAGE_DIR)
            if (!outputDir.exists()) outputDir.mkdirs()

            val outputFile = File(outputDir, "img_${System.currentTimeMillis()}.jpg")
            var quality = Constants.IMAGE_COMPRESS_QUALITY

            do {
                val baos = ByteArrayOutputStream()
                correctedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                if (baos.size() <= maxSize || quality <= 10) {
                    FileOutputStream(outputFile).use { it.write(baos.toByteArray()) }
                    break
                }
                quality -= 10
            } while (true)

            if (correctedBitmap != bitmap) correctedBitmap.recycle()
            bitmap.recycle()
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    fun cropToSquare(context: Context, uri: Uri): File? {
        return try {
            val bitmap = decodeSampledBitmap(context, uri, MAX_AVATAR_SIZE)
                ?: return null
            val correctedBitmap = correctOrientation(context, uri, bitmap)

            val size = minOf(correctedBitmap.width, correctedBitmap.height)
            val x = (correctedBitmap.width - size) / 2
            val y = (correctedBitmap.height - size) / 2
            val cropped = Bitmap.createBitmap(correctedBitmap, x, y, size, size)

            val outputDir = File(context.filesDir, Constants.AVATAR_DIR)
            if (!outputDir.exists()) outputDir.mkdirs()

            val outputFile = File(outputDir, "avatar_temp_${System.currentTimeMillis()}.jpg")
            val baos = ByteArrayOutputStream()
            cropped.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_COMPRESS_QUALITY, baos)
            FileOutputStream(outputFile).use { it.write(baos.toByteArray()) }

            if (correctedBitmap != bitmap) correctedBitmap.recycle()
            cropped.recycle()
            bitmap.recycle()
            outputFile
        } catch (e: Exception) {
            null
        }
    }

    fun saveAvatar(context: Context, sourceUri: Uri, userId: Long): String {
        return try {
            val bitmap = decodeSampledBitmap(context, sourceUri, MAX_AVATAR_SIZE)
                ?: return ""
            val destFile = getAvatarFile(context, userId)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_COMPRESS_QUALITY, baos)
            FileOutputStream(destFile).use { it.write(baos.toByteArray()) }
            bitmap.recycle()
            destFile.absolutePath
        } catch (e: Exception) {
            ""
        }
    }

    private fun decodeSampledBitmap(context: Context, uri: Uri, maxDimension: Int): Bitmap? {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }

        val (width, height) = options.run { outWidth to outHeight }
        if (width <= 0 || height <= 0) return null

        var sampleSize = 1
        while (width / sampleSize > maxDimension || height / sampleSize > maxDimension) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }

    private fun correctOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                else -> return bitmap
            }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            bitmap
        }
    }

    fun getAvatarFile(context: Context, userId: Long): File {
        val dir = File(context.filesDir, Constants.AVATAR_DIR)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "avatar_$userId.jpg")
    }
}
