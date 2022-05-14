package xyz.flussigkatz.spoonzilla.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import xyz.flussigkatz.core_api.AppProvider
import javax.inject.Singleton


@Singleton
@Component
interface AppComponent : AppProvider {
    companion object {
        private var appComponent: AppProvider? = null
        fun create(application: Application): AppProvider {
            return appComponent ?: DaggerAppComponent
                .builder()
                .application(application)
                .build().also {
                    appComponent = it
                }
        }
    }

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}