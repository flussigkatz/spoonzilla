package xyz.flussigkatz.core_api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "dishes_alarms", indices = [Index(value = ["localId"], unique = true)])
data class DishAlarm(
    @PrimaryKey(autoGenerate = true)  val localId: Int = 0,
    @ColumnInfo(name = "id")val id: Int,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "image")val image: String?,
    @ColumnInfo(name = "alarmTime")var alarmTime: Long,
) : Parcelable