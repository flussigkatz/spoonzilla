package xyz.flussigkatz.core_api.entity.ingredients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class IngredientItem(
    val image: String?,
    val name: String?,
    val unit: String?,
    var value: Double?,
) : Parcelable