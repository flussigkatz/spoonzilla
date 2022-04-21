package xyz.flussigkatz.spoonzilla

import android.app.Application
import timber.log.Timber
import xyz.flussigkatz.core.CoreProvidersFactory
import xyz.flussigkatz.remote.DaggerRemoteComponent
import xyz.flussigkatz.spoonzilla.view.notification.NotificationHelper
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
        initTimber()
        NotificationHelper.initNotification(this)
    }

    private fun initDagger() {
        dagger = DaggerMainComponent.builder()
            .appProvider(AppComponent.create(this))
            .remoteProvider(DaggerRemoteComponent.create())
            .databaseProvider(CoreProvidersFactory.createDatabaseBuilder(AppComponent.create(this)))
            .domainModule(DomainModule())
            .build()
    }

    private fun initTimber() {
        if(BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var instance: App
            private set
    }
}