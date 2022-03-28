package xyz.flussigkatz.core_api.entity.instructions

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize


@Parcelize
data class InstructionsItem(
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "steps") val steps: List<Step>
): Parcelable