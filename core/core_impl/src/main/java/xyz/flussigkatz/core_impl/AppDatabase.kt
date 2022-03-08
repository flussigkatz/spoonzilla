package xyz.flussigkatz.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.core_api.db.DatabaseContract
import xyz.flussigkatz.core_api.entity.Dish

@Database(entities = [Dish::class], version = 1)
abstract class AppDatabase : RoomDatabase(), DatabaseContract