package xyz.flussigkatz.core_api

import android.app.Application

interface AppProvider {
    fun provideApplication(): Application
}