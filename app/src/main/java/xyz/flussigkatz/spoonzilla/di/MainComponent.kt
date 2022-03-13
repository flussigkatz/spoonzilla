package xyz.flussigkatz.spoonzilla.di

import dagger.Component
import xyz.flussigkatz.core_api.AppProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.remote.RemoteProvider
import xyz.flussigkatz.spoonzilla.di.module.DialogModule
import xyz.flussigkatz.spoonzilla.di.module.DomainModule
import xyz.flussigkatz.spoonzilla.viewmodel.*
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class, RemoteProvider::class, DatabaseProvider::class],
    modules = [DomainModule::class, DialogModule::class]
)

interface MainComponent {

    fun inject(mainActivityViewModel: MainActivityViewModel)

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(cuisineDialogFragmentViewModel: CuisineDialogFragmentViewModel)

    fun inject(dietsDialogFragmentViewModel: DietsDialogFragmentViewModel)

    fun inject(intolerancesDialogFragmentViewModel: IntolerancesDialogFragmentViewModel)

    fun inject(mealTypesDialogFragmentViewModel: MealTypesDialogFragmentViewModel)

    fun inject(advancedSearchSettingsFragmentViewModel: AdvancedSearchSettingsFragmentViewModel)

    fun inject(advancedSearchFragmentViewModel: AdvancedSearchFragmentViewModel)

}