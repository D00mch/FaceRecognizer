package com.livermor.face

import android.app.Application
import com.getkeepsafe.relinker.ReLinker
import com.livermor.face.dagger.AppComponent
import com.livermor.face.dagger.AppModule
import com.livermor.face.dagger.BmpHelpModule
import com.livermor.face.dagger.DaggerAppComponent


class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }


    override fun onCreate() {
        super.onCreate()

        ReLinker.loadLibrary(this, "stasm")

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .bmpHelpModule(BmpHelpModule())
                .build()
    }
}