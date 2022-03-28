package xyz.flussigkatz.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cashed_marked_dishes", indices = [Index(value = ["id"], unique = true)])
data class DishMarked(
    @PrimaryKey(autoGenerate = true)  val localId: Int = 0,
    @ColumnInfo(name = "id")val id: Int,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "image")val image: String?,
    @ColumnInfo(name = "mark")val mark: Boolean
) : Parcelable