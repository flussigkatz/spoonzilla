package xyz.flussigkatz.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cashed_dishes", indices = [Index(value = ["title"], unique = false)])
data class Dish(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")val id: Int,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "image")val image: String?
) : Parcelable