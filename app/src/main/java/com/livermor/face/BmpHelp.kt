package com.livermor.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

private val TAG: String = "BmpHelper"

fun storeImage(image: Bitmap, imgPath: String): Boolean {

    try {
        val fos = FileOutputStream(imgPath)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return true
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "File not found: " + e.message)
    } catch (e: IOException) {
        Log.e(TAG, "Error accessing file: " + e.message)
    }

    return false
}

fun fitToTheScreenSize(src: Bitmap, context: Context): Bitmap {

    val scale: Float

    val bitmapWidth = src.width
    val bitmapHeight = src.height


    //get status bar height
    var statusBar = 0
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        statusBar = context.resources.getDimensionPixelSize(resourceId)
    }
    val neededHeight = context.resources.displayMetrics.heightPixels - statusBar
    val neededWidth = context.resources.displayMetrics.widthPixels


    scale = neededWidth.toFloat() / bitmapWidth
    val m = Matrix()
    m.setScale(scale, scale)

    //cropping
    var yPoint = 0f
    val bitmapNewHeight = bitmapHeight * scale
    var bitmapHeightRatio = 1f
    if (bitmapNewHeight > neededHeight) {
        val heightDelta = bitmapNewHeight - neededHeight
        yPoint = heightDelta / 2
        bitmapHeightRatio = (bitmapNewHeight - heightDelta) / bitmapNewHeight
    }

    return Bitmap.createBitmap(src,
            0, yPoint.toInt(),
            src.width, (src.height * bitmapHeightRatio).toInt(),
            m, false)
}

fun getBmpWithProperRotation(path: String): Bitmap {

    val bounds = BitmapFactory.Options()
    bounds.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, bounds)

    val opts = BitmapFactory.Options()
    opts.inMutable = true
    val bm = BitmapFactory.decodeFile(path, opts)
    var exif: ExifInterface? = null
    try {
        exif = ExifInterface(path)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    var orientString: String? = null
    if (exif != null) {
        orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
    }
    val orientation = if (orientString != null)
        Integer.parseInt(orientString)
    else
        ExifInterface.ORIENTATION_NORMAL

    var rotationAngle = 0
    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
        Log.w(TAG, "ORIENTATION_ROTATE_90")
        rotationAngle = 90
    }
    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
        rotationAngle = 180
        Log.w(TAG, "ORIENTATION_ROTATE_180")
    }
    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
        rotationAngle = 270
        Log.w(TAG, "ORIENTATION_ROTATE_270")
    }

    // My test
    if (rotationAngle == 0) {
        Log.w(TAG, "ORIENTATION_ROTATE_0")
    }

    val matrix = Matrix()
    matrix.setRotate(rotationAngle.toFloat(), bm.width.toFloat() / 2, bm.height.toFloat() / 2)

    return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true)
}

