package xyz.flussigkatz.spoonzilla.data.entity.instructions

import xyz.flussigkatz.spoonzilla.data.entity.Equipment
import xyz.flussigkatz.spoonzilla.data.entity.Ingredient


data class Step(
    val equipment: List<Equipment>,
    val ingredients: List<Ingredient>,
    val number: Int,
    val step: String,
    val numberLength: Int?,
    val unitLength: String?
)