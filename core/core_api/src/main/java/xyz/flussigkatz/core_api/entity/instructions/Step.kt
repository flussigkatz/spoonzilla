package xyz.flussigkatz.core_api.entity.instructions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import xyz.flussigkatz.core_api.entity.equipments.EquipmentItem
import xyz.flussigkatz.core_api.entity.ingredients.IngredientItem

@Parcelize
data class Step(
    val equipmentItems: List<EquipmentItem>,
    val ingredientItems: List<IngredientItem>,
    val number: Int,
    val step: String,
    val numberLength: Int?,
    val unitLength: String?
) : Parcelable