package xyz.flussigkatz.core_api.entity.ingredients

import androidx.room.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo


@Entity(
    tableName = "cashed_dishes_ingredients",
    indices = [Index(value = ["dishId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = DishAdvancedInfo::class,
        parentColumns = ["id"],
        childColumns = ["dishId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Ingredients(
    @PrimaryKey val dishId: Int = 0,
    @ColumnInfo(name = "ingredient") val ingredient: String
)