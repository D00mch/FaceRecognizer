package com.livermor.face.dagger

import com.livermor.face.screen.face.FaceDotsViewModel
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