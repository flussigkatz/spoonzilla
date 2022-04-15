package xyz.flussigkatz.core_impl

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.flussigkatz.core_api.db.DatabaseContract
import xyz.flussigkatz.core_api.entity.Dish
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo
import xyz.flussigkatz.core_api.entity.DishAlarm
import xyz.flussigkatz.core_api.entity.DishMarked
import xyz.flussigkatz.core_api.entity.equipments.Equipments
import xyz.flussigkatz.core_api.entity.ingredients.Ingredients
import xyz.flussigkatz.core_api.entity.instructions.Instructions
import xyz.flussigkatz.core_api.entity.nutrient.Nutrients

@Database(
    entities = [
        Dish::class,
        DishMarked::class,
        DishAdvancedInfo::class,
        DishAlarm::class,
        Ingredients::class,
        Equipments::class,
        Instructions::class,
        Nutrients::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase(), DatabaseContract