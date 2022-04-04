package xyz.flussigkatz.core_api.entity.nutrient

import androidx.room.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo


@Entity(
    tableName = "cashed_dishes_nutrients",
    indices = [Index(value = ["dishId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = DishAdvancedInfo::class,
        parentColumns = ["id"],
        childColumns = ["dishId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Nutrients(
    @PrimaryKey val dishId: Int = 0,
    @ColumnInfo(name = "nutrientItems") val nutrientItems: String
)