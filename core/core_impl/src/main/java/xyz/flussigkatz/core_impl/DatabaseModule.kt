package xyz.flussigkatz.core_impl

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.flussigkatz.core_api.db.DatabaseContract
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDishDatabase(application: Application): DatabaseContract =
        Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideDishesDao(databaseContract: DatabaseContract) = databaseContract.dishesDao()

    companion object {
        private const val DATABASE_NAME = "dishes_db"
    }
}