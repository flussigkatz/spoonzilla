package xyz.flussigkatz.spoonzilla

import android.app.Application
import xyz.flussigkatz.core.CoreProvidersFactory
import xyz.flussigkatz.remote.DaggerRemoteComponent
import xyz.flussigkatz.spoonzilla.di.AppComponent
import xyz.flussigkatz.spoonzilla.di.DaggerMainComponent
import xyz.flussigkatz.spoonzilla.di.MainComponent
import xyz.flussigkatz.spoonzilla.di.module.DomainModule

class App : Application() {

    lateinit var dagger: MainComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDagger()

    }

    private fun initDagger() {
        dagger = DaggerMainComponent.builder()
            .appProvider(AppComponent.create(this))
            .remoteProvider(DaggerRemoteComponent.create())
            .databaseProvider(CoreProvidersFactory.createDatabaseBuilder(AppComponent.create(this)))
            .domainModule(DomainModule())
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}