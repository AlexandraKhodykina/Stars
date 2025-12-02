package com.hfad.stars
import android.app.Application

class App : Application() {

    companion object {
        private var _instance: App? = null
        val instance: App
            get() = _instance ?: throw IllegalStateException("Application не инициализирован")
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this

        // Можно добавить инициализацию здесь, если нужно
        // Например, инициализацию базы данных или других компонентов
    }
}