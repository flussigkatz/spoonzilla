package xyz.flussigkatz.spoonzilla.data.db

import xyz.flussigkatz.spoonzilla.data.db.DataBaseContract

class RecipeDataBase : DataBaseContract {
    private val db = listOf(1, 2, 3)

    override fun returnDataBase(): List<Int> {
        return db
    }
}