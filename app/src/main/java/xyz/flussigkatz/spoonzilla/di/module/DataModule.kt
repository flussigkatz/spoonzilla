package xyz.flussigkatz.spoonzilla.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

}