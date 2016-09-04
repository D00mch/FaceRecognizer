package com.livermor.face.screen.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.livermor.face.R
import com.livermor.face.fitToTheScreenSize
import com.livermor.face.getBmpWithProperRotation
import com.livermor.face.stasm.Stasm
import com.livermor.face.storeImage
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FaceDotsViewModel(val context: Context, val imagePath: String) {
    private val TAG = FaceDotsViewModel::class.java.simpleName

    val imageSubject: PublishSubject<Bitmap> by lazy { PublishSubject.create<Bitmap>() }
    val errorSubject: PublishSubject<String> by lazy { PublishSubject.create<String>() }


    private val dataDirectory: File by lazy { context.getDir("data", Context.MODE_PRIVATE) }
    private val faceFile: File by lazy { File(dataDirectory, "haarcascade_frontalface_alt2.xml") }
    private val leftEye: File by lazy { File(dataDirectory, "haarcascade_mcs_lefteye.xml") }
    private val rightEye: File by lazy { File(dataDirectory, "haarcascade_mcs_righteye.xml") }


    init {
        loadResources()

        getDrawDotsOnFaceObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ bitmap ->
                    imageSubject.onNext(bitmap)
                }, { thr ->
                    Log.e(TAG, thr.toString())
                    errorSubject.onNext(thr.toString())
                })
    }


    private fun getDrawDotsOnFaceObservable(): Observable<Bitmap> =
            Observable.create<Bitmap> { sub ->

                var bmp = getBmpWithProperRotation(imagePath)
                bmp = fitToTheScreenSize(bmp, context)
                storeImage(bmp, imagePath)
                val srcPoints: IntArray? = Stasm.FindFaceDots(1f, 1f, imagePath)


                // handle possible error
                if (srcPoints != null && srcPoints.size != 0) {

                    when {
                        srcPoints[0] == -1 && srcPoints[1] == -1 ->
                            sub.onError(Throwable("Cannot load image file"))

                        srcPoints[0] == -2 && srcPoints[1] == -2 ->
                            sub.onError(Throwable("Error in stasm_search_single!"))

                        srcPoints[0] == -3 && srcPoints[1] == -3 ->
                            sub.onError(Throwable("No face found"))
                    }

                    bmp.drawPoints(srcPoints)
                    sub.onNext(bmp)
                    sub.onCompleted()

                } else {
                    sub.onError(Throwable("Something went wrong"))
                }
            }


    private fun Bitmap.drawPoints(srcPoints: IntArray) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.CYAN
        paint.strokeWidth = 5f

        val canvas = Canvas(this)
        var i = 0
        val j = srcPoints.size
        while (i < j / 2) {
            val x = srcPoints[2 * i].toFloat()
            val y = srcPoints[2 * i + 1].toFloat()
            canvas.drawPoint(x, y, paint)
            i++
        }
    }


    //—————————————————————————————————————————————————————————————————————— preparations

    private fun loadResources() {
        putDataFileInLocalDir(R.raw.haarcascade_frontalface_alt2, faceFile)
        putDataFileInLocalDir(R.raw.haarcascade_mcs_lefteye, leftEye)
        putDataFileInLocalDir(R.raw.haarcascade_mcs_righteye, rightEye)
    }

    private fun putDataFileInLocalDir(id: Int, f: File) {
        try {
            val inputStream = context.resources.openRawResource(id)
            val os = FileOutputStream(f, false)
            val buffer = ByteArray(4096)
            var bytesRead: Int = inputStream.read(buffer)
            while (bytesRead != -1) {
                os.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
            inputStream.close()
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}