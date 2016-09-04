package com.livermor.face

import android.app.Application
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
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .bmpHelpModule(BmpHelpModule())
                .build()
    }
}