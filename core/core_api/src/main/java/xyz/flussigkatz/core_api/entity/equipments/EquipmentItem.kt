package xyz.flussigkatz.core_api.entity.equipments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EquipmentItem(
    val image: String?,
    val name: String,
) : Parcelable