package com.livermor.face

import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.getkeepsafe.relinker.ReLinker
import com.livermor.face.stasm.Stasm
import kotlinx.android.synthetic.main.activity_result.*
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ResultActivity : AppCompatActivity() {

    companion object {
        private val TAG = ResultActivity::class.java.simpleName
        val EXTRA_IMG_PATH = "com.livermor.face.EXTRA_IMG_PATH"
    }


    private val src: Bitmap
            by lazy { BitmapFactory.decodeResource(resources, R.raw.main_model) }


    private val dataDirectory by lazy {getDir("data", Context.MODE_PRIVATE)}
    private val faceFile by lazy { File(dataDirectory, "haarcascade_frontalface_alt2.xml") }
    private val leftEye  by lazy { File(dataDirectory, "haarcascade_mcs_lefteye.xml") }
    private val rightEye by lazy { File(dataDirectory, "haarcascade_mcs_righteye.xml") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReLinker.loadLibrary(this, "stasm");

        setContentView(R.layout.activity_result)

        ImageProcessTask().execute()
    }


    private inner class ImageProcessTask : AsyncTask<Void, Void, Bitmap>() {

        override fun onPreExecute() {
            super.onPreExecute()
            // haar cascade file loading
            loadResources()
        }

        override fun doInBackground(vararg params: Void): Bitmap? {


            Log.i(TAG, String.format("Original Image Size: %d x %d", src.width, src.height))
            val path = saveImage(src)

            //val scale = calculateScale(this@ResultActivity, src)
            val scale = 1f
            val m = Matrix()
            m.setScale(scale, scale)

            val scaledSrc = Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, false)
            Log.d(TAG, String.format("Scaled Image Size: %d x %d", scaledSrc.width, scaledSrc.height))

            var srcPoints: IntArray? = null
            try {
                // Stasm을 이용해 얼굴의 landmark 포인트를 가져옴
                srcPoints = Stasm.FindFaceDots(scale, scale, path)
            } catch (e: UnsatisfiedLinkError) {
                e.printStackTrace()
            }

            Log.i(TAG, "points == " + srcPoints!!
                    .last());

            // handle possible error
            if (srcPoints == null || srcPoints.size == 0) {
                return null
            }
            if (srcPoints[0] == -1 && srcPoints[1] == -1) {
                Log.e(TAG, "Cannot load image file")
                return null
            } else if (srcPoints[0] == -2 && srcPoints[1] == -2) {
                Log.e(TAG, "Error in stasm_search_single!")
                return null
            } else if (srcPoints[0] == -3 && srcPoints[1] == -3) {
                Log.e(TAG, "No face found")
                return null
            }

            // 이미지 위에 가져온 landmark 포인트를 표시
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.CYAN
            paint.strokeWidth = 5f

            val canvas = Canvas(scaledSrc)
            var i = 0
            val j = srcPoints.size
            while (i < j / 2) {
                val x = srcPoints[2 * i].toFloat()
                val y = srcPoints[2 * i + 1].toFloat()
                canvas.drawPoint(x, y, paint)
                i++
            }
            return scaledSrc
        }

        override fun onPostExecute(scaledSrc: Bitmap) {
            super.onPostExecute(scaledSrc)
            Log.i(TAG, "scaledSrs height = " + scaledSrc.height + ", width = " + scaledSrc.width)
            imageView.setImageBitmap(scaledSrc)
        }

    }


    private fun loadResources() {
        putDataFileInLocalDir(R.raw.haarcascade_frontalface_alt2, faceFile)
        putDataFileInLocalDir(R.raw.haarcascade_mcs_lefteye, leftEye)
        putDataFileInLocalDir(R.raw.haarcascade_mcs_righteye, rightEye)

    }

    private fun putDataFileInLocalDir(id: Int, f: File) {
        try {
            val inputStream = resources.openRawResource(id)
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

    private fun calculateScale(context: Context, src: Bitmap): Float {

        var ratio: Float
        val width = src.width
        val height = src.height
        val screenHeight = context.resources.displayMetrics.heightPixels
        Log.i(TAG, "screenHeight  = " + screenHeight)
        val screenWidth = context.resources.displayMetrics.widthPixels
        Log.i(TAG, "screenWidth  = " + screenWidth)
        ratio = screenWidth.toFloat() / width
        if (height > width) {
            ratio = (screenHeight / height).toFloat()
        } else {
            ratio = (screenWidth / width).toFloat()
        }
        return ratio
    }

    // save file in a temporary folder, and if it already exist delete it preliminary
    private fun saveImage(bitmap: Bitmap): String {
        val direct = File(cacheDir, "temp")
        if (!direct.exists()) {
            val directory = File(direct.absolutePath)
            directory.mkdirs()
        }
        val file = File(File(direct.absolutePath), "sample.jpg")
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file, false)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file.absolutePath
    }


    fun resizeBitmap(bitmap: Bitmap, faceWidth: Int, faceHeight: Int): Bitmap {

        return Bitmap.createScaledBitmap(
                bitmap,
                faceWidth,
                faceHeight,
                false)
    }
}
