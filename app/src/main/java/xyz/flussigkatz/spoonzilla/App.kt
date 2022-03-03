package xyz.flussigkatz.spoonzilla

import android.app.Application
import xyz.flussigkatz.remote.DaggerRemoteComponent
import xyz.flussigkatz.spoonzilla.di.AppComponent
import xyz.flussigkatz.spoonzilla.di.DaggerAppComponent
import xyz.flussigkatz.spoonzilla.di.module.ContextModule
import xyz.flussigkatz.spoonzilla.di.module.DomainModule

class App : Application() {

    lateinit var dagger: AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        dagger = DaggerAppComponent.builder()
            .remoteProvider(DaggerRemoteComponent.create())
            .contextModule(ContextModule(this))
            .domainModule(DomainModule())
            .build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}