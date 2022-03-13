package xyz.flussigkatz.spoonzilla.di.module

import dagger.Module
import dagger.Provides
import xyz.flussigkatz.spoonzilla.view.dialog.CuisineDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.DietsDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.IntolerancesDialogFragment
import xyz.flussigkatz.spoonzilla.view.dialog.MealTypesDialogFragment
import javax.inject.Singleton

@Module
class DialogModule {

    @Singleton
    @Provides
    fun provideCuisineDialogFragment() = CuisineDialogFragment()

    @Singleton
    @Provides
    fun provideDietsDialogFragment() = DietsDialogFragment()

    @Singleton
    @Provides
    fun provideIntolerancesDialogFragment() = IntolerancesDialogFragment()

    @Singleton
    @Provides
    fun provideMealTypesDialogFragment() = MealTypesDialogFragment()
}