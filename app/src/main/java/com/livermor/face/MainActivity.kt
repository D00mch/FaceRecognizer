package com.livermor.face

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, ResultActivity::class.java))

        button.setOnClickListener {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            val photo = data.extras.get("data") as Bitmap

            try {
                //Write file
                val filename = "bitmap.png"
                val stream = this.openFileOutput(filename, Context.MODE_PRIVATE)
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream)

                //Cleanup
                stream.close()
                photo.recycle()

                //Pop intent
                val in1 = Intent(this, ResultActivity::class.java)
                in1.putExtra(ResultActivity.EXTRA_IMG_PATH, filename)
                startActivity(in1)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        private val CAMERA_REQUEST = 1888
    }
}
