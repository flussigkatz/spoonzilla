package xyz.flussigkatz.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cashed_dishes_advanced_info", indices = [Index(value = ["id"], unique = true)])
data class DishAdvancedInfo(
    @PrimaryKey(autoGenerate = true)  val localId: Int = 0,
    @ColumnInfo(name = "id")val id: Int,
    @ColumnInfo(name = "aggregateLikes") val aggregateLikes: Int,
    @ColumnInfo(name = "cheap") val cheap: Boolean,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "pricePerServing") val pricePerServing: Double,
    @ColumnInfo(name = "readyInMinutes") val readyInMinutes: Int,
    @ColumnInfo(name = "servings") val servings: Int,
    @ColumnInfo(name = "sourceUrl") val sourceUrl: String,
    @ColumnInfo(name = "summary") val summary: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "mark") var mark: Boolean
) : Parcelable