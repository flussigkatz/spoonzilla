package xyz.flussigkatz.core_api.entity.instructions

import androidx.room.*
import xyz.flussigkatz.core_api.entity.DishAdvancedInfo

@Entity(
    tableName = "cashed_dishes_instructions",
    indices = [Index(value = ["dishId"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = DishAdvancedInfo::class,
        parentColumns = ["id"],
        childColumns = ["dishId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Instructions (
    @PrimaryKey val dishId: Int = 0,
    @ColumnInfo(name = "instructionsItems") val instructionsItems: String
)