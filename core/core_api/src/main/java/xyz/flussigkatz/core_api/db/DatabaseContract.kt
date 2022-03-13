package xyz.flussigkatz.core_api.db

interface DatabaseContract {
    fun dishesDao(): DishDao
}