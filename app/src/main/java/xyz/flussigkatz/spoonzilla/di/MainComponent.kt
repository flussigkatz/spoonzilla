package xyz.flussigkatz.spoonzilla.di

import dagger.Component
import xyz.flussigkatz.core_api.AppProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.remote.RemoteProvider
import xyz.flussigkatz.spoonzilla.SpoonzillaReceiver
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

    fun inject(markedFragmentViewModel: MarkedFragmentViewModel)

    fun inject(cuisineDialogFragmentViewModel: CuisineDialogFragmentViewModel)

    fun inject(dietsDialogFragmentViewModel: DietsDialogFragmentViewModel)

    fun inject(intolerancesDialogFragmentViewModel: IntolerancesDialogFragmentViewModel)

    fun inject(mealTypesDialogFragmentViewModel: MealTypesDialogFragmentViewModel)

    fun inject(advancedSearchSettingsFragmentViewModel: AdvancedSearchSettingsFragmentViewModel)

    fun inject(advancedSearchFragmentViewModel: AdvancedSearchFragmentViewModel)

    fun inject(dishOverviewFragmentViewModel: DishOverviewFragmentViewModel)

    fun inject(dishIngredientsFragmentViewModel: DishIngredientsFragmentViewModel)

    fun inject(dishInstructionsFragmentViewModel: DishInstructionsFragmentViewModel)

    fun inject(dishEquipmentsFragmentViewModel: DishEquipmentsFragmentViewModel)

    fun inject(dishNutrientFragmentViewModel: DishNutrientFragmentViewModel)

    fun inject(dishAlarmFragmentViewModel: DishAlarmFragmentViewModel)

    fun inject(settingsFragmentViewModel: SettingsFragmentViewModel)

    fun inject(SpoonzillaReceiver: SpoonzillaReceiver)

}