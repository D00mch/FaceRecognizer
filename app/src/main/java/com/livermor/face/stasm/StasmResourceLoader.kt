package com.livermor.face.stasm

import android.content.Context
import com.getkeepsafe.relinker.ReLinker
import com.livermor.face.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class StasmResourceLoader(private val context: Context) {

    companion object {
        const private val TAG = "StasmResourceLoader"

    }

    private val dataDirectory: File by lazy { context.getDir("data", Context.MODE_PRIVATE) }
    private val faceFile: File by lazy { File(dataDirectory, "haarcascade_frontalface_alt2.xml") }
    private val leftEye: File by lazy { File(dataDirectory, "haarcascade_mcs_lefteye.xml") }
    private val rightEye: File by lazy { File(dataDirectory, "haarcascade_mcs_righteye.xml") }

    fun prepareResources() {

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