package com.livermor.face.dagger

import android.content.Context
import com.livermor.face.stasm.StasmResourceLoader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class StasmResLoaderModule {
    @Provides
    @Singleton
    fun provideStasmResLoader(context: Context) = StasmResourceLoader(context)
}