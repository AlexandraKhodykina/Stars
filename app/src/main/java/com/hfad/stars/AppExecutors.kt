package com.hfad.stars
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// AppExecutors - управление потоками выполнения задач.
// Разделяет задачи на три типа потоков для оптимальной работы.
class AppExecutors {
    // Для операций с диском (база данных)
    private val diskIO: Executor = Executors.newSingleThreadExecutor()

    // Для сетевых операций
    private val networkIO: Executor = Executors.newFixedThreadPool(3)

    // Для обновления UI (главный поток)
    private val mainThread: Executor = MainThreadExecutor()

    fun diskIO(): Executor = diskIO
    fun networkIO(): Executor = networkIO
    fun mainThread(): Executor = mainThread

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppExecutors? = null

        fun getInstance(): AppExecutors {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppExecutors().also { INSTANCE = it }
            }
        }
    }

}