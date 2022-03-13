package xyz.flussigkatz.spoonzilla.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import xyz.flussigkatz.core_api.db.DishDao
import xyz.flussigkatz.remote.SpoonacularApi
import xyz.flussigkatz.spoonzilla.data.db.MainRepository
import xyz.flussigkatz.spoonzilla.data.preferences.PreferenceProvider
import xyz.flussigkatz.spoonzilla.domain.Interactor
import javax.inject.Singleton

@Module
class DomainModule {

    @Singleton
    @Provides
    fun providePreferences(context: Context) = PreferenceProvider(context)

    @Singleton
    @Provides
    fun provideRepository(dishDao: DishDao) = MainRepository(dishDao)

    @Provides
    @Singleton
    fun provideSearchPublishSubject(): PublishSubject<String> = PublishSubject.create()

    @Provides
    @Singleton
    fun provideBehaviorSubjectLoadingState(): BehaviorSubject<Boolean> = BehaviorSubject.create()

    @Provides
    @Singleton
    fun provideInteractor(
        repository: MainRepository,
        searchPublishSubject: PublishSubject<String>,
        loadingState: BehaviorSubject<Boolean>,
        preferences: PreferenceProvider,
        retrofitService: SpoonacularApi

    ) = Interactor(
        repository = repository,
        searchPublishSubject = searchPublishSubject,
        loadingState = loadingState,
        preferences = preferences,
        retrofitService = retrofitService
    )
}