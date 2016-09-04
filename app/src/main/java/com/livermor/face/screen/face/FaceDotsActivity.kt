package com.livermor.face.screen.face

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.getkeepsafe.relinker.ReLinker
import com.livermor.face.R
import kotlinx.android.synthetic.main.activity_result.*

class FaceDotsActivity : AppCompatActivity() {

    companion object {
        private val TAG = FaceDotsActivity::class.java.simpleName
        private val EXTRA_IMG_PATH = "com.livermor.face.EXTRA_IMG_PATH"

        fun intent(context: Context, imgPath: String): Intent {
            val intent = Intent(context, FaceDotsActivity::class.java)
            intent.putExtra(EXTRA_IMG_PATH, imgPath)
            return intent
        }
    }

    private val imgPath: String by lazy { intent.getStringExtra(EXTRA_IMG_PATH) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReLinker.loadLibrary(this, "stasm");
        setContentView(R.layout.activity_result)

        val faceViewModel = FaceDotsViewModel(imgPath)
        faceViewModel.imageSubject.subscribe { imageView.setImageBitmap(it) }
    }
}
