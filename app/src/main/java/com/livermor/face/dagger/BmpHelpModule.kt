package com.livermor.face.dagger

import android.content.Context
import com.livermor.face.util.BmpHelp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class BmpHelpModule {

    @Provides
    @Singleton
    fun provideBmpHelp(context: Context) = BmpHelp(context)
}