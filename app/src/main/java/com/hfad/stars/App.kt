package com.hfad.stars
import android.app.Application

class App : Application() {

    companion object {
        private var instance: App? = null

        fun getInstance(): App {
            return instance ?: throw IllegalStateException("Application не инициализирован")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}