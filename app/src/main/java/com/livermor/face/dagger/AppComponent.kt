package com.livermor.face.dagger

import com.livermor.face.screen.face.FaceDotsActivity
import com.livermor.face.screen.face.FaceDotsViewModel
import com.livermor.face.util.BmpHelp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AppModule::class,
        BmpHelpModule::class,
        StasmResLoaderModule::class
))
interface AppComponent {
    fun inject(faceDotsActivity: FaceDotsViewModel)
}