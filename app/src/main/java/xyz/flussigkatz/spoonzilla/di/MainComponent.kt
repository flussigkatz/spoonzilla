package xyz.flussigkatz.spoonzilla.di

import dagger.Component
import xyz.flussigkatz.core_api.AppProvider
import xyz.flussigkatz.core_api.db.DatabaseProvider
import xyz.flussigkatz.remote.RemoteProvider
import xyz.flussigkatz.spoonzilla.di.module.DomainModule
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.viewmodel.CuisineDialogFragmentViewModel
import xyz.flussigkatz.spoonzilla.viewmodel.HomeFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class, RemoteProvider::class, DatabaseProvider::class],
    modules = [DomainModule::class]
)

interface MainComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(cuisineDialogFragmentViewModel: CuisineDialogFragmentViewModel)

}