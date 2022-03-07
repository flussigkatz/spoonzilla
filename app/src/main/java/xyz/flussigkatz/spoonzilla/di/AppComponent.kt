package xyz.flussigkatz.spoonzilla.di


import dagger.Component
import xyz.flussigkatz.remote.RemoteProvider
import xyz.flussigkatz.spoonzilla.di.module.ContextModule
import xyz.flussigkatz.spoonzilla.di.module.DomainModule
import xyz.flussigkatz.spoonzilla.view.MainActivity
import xyz.flussigkatz.spoonzilla.viewmodel.CuisineDialogFragmentViewModel
import xyz.flussigkatz.spoonzilla.viewmodel.HomeFragmentViewModel
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [RemoteProvider::class],
    modules = [ContextModule::class, DomainModule::class]
)

interface AppComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(homeFragmentViewModel: HomeFragmentViewModel)

    fun inject(cuisineDialogFragmentViewModel: CuisineDialogFragmentViewModel)
}