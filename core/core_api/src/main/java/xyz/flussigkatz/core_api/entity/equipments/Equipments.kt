package xyz.flussigkatz.core_api.entity.equipments

import androidx.room.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo

@Entity(
    tableName = "cashed_dishes_equipments",
    indices = [Index(value = ["dishId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = DishAdvancedInfo::class,
        parentColumns = ["id"],
        childColumns = ["dishId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Equipments(
    @PrimaryKey val dishId: Int = 0,
    @ColumnInfo(name = "equipment") val equipment: String
)