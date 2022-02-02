package xyz.flussigkatz.spoonzilla.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(val app: Application) {

    @Provides
    fun provideContext(): Context = app.applicationContext

}