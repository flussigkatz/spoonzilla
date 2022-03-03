package xyz.flussigkatz.spoonzilla.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.remote.SpoonacularApi
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule {

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Provides
    @Singleton
    fun provideSearchPublishSubject(): PublishSubject<String?> = PublishSubject.create()

    @Provides
    @Singleton
    fun provideInteractor(
        searchPublishSubject: PublishSubject<String?>,
        preferences: PreferenceProvider,
        retrofitService: SpoonacularApi

    ) = Interactor(
        searchPublishSubject = searchPublishSubject,
        preferences = preferences,
        retrofitService = retrofitService
    )
}