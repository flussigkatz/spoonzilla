package xyz.flussigkatz.spoonzilla.di


import dagger.Component
import xyz.flussigkatz.spoonzilla.di.module.ContextModule
import xyz.flussigkatz.spoonzilla.di.module.DataModule
import xyz.flussigkatz.spoonzilla.view.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ContextModule::class, DataModule::class]
)

interface AppComponent {

    fun inject(mainActivity: MainActivity)

}