package com.livermor.face.screen

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.livermor.face.R
import com.livermor.face.screen.face.FaceDotsActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    val TAG: String = MainActivity::class.java.simpleName

    private val path: String
            by lazy { getExternalFilesDir(null).toString() + getString(R.string.photo_name) }
    private val imageUri: Uri by lazy { Uri.fromFile(File(path)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Log.i(TAG, "path == " + path)
        Log.i(TAG, "imageUri == " + imageUri)

        button.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "result is ok")
            startActivity(FaceDotsActivity.intent(this, path))

        } else if (resultCode == Activity.RESULT_CANCELED) {
//            MyToast.show("вы вышли из камеры")
        }
    }

    companion object {

        private val CAMERA_REQUEST = 1888
    }
}
