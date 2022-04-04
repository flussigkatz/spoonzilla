package xyz.flussigkatz.core_api.entity.nutrient

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class NutrientItem(
    val amount: String,
    val percentOfDailyNeeds: Double,
    val title: String,
    val good: Boolean
) : Parcelable