package xyz.flussigkatz.core_api.db

interface DatabaseProvider {
    fun dishesDao(): DishDao
}