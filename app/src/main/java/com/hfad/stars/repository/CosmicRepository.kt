package com.hfad.stars.repository
import android.content.Context
import androidx.lifecycle.LiveData
import com.hfad.stars.App
import com.hfad.stars.api.NASAApiService
import com.hfad.stars.database.AppDatabase
import com.hfad.stars.database.CosmicObjectDao
import com.hfad.stars.model.CosmicObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class CosmicRepository(context: Context) {

    private val dao: CosmicObjectDao = AppDatabase.getDatabase(context).cosmicObjectDao()
    private val appExecutors = App.getInstance().appExecutors

    private val nasaService: NASAApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NASAApiService::class.java)
    }

    // Получить все объекты
    suspend fun getAllObjects(forceRefresh: Boolean = false): LiveData<List<CosmicObject>> {
        return withContext(Dispatchers.IO) {
            // Проверяем, нужно ли обновить из сети
            if (forceRefresh) {
                try {
                    loadFromApi()
                } catch (e: Exception) {
                    // В случае ошибки возвращаем локальные данные
                }
            }
            dao.getAllObjects()
        }
    }

    // Получить избранные
    fun getFavorites(): LiveData<List<CosmicObject>> {
        return dao.getFavorites()
    }

    // Поиск
    fun search(query: String): LiveData<List<CosmicObject>> {
        return dao.searchObjects(query)
    }

    // Получить по ID
    suspend fun getObjectById(id: String): CosmicObject? {
        return withContext(Dispatchers.IO) {
            dao.getObjectById(id)
        }
    }

    // Переключить избранное
    suspend fun toggleFavorite(objectId: String, isCurrentlyFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            dao.updateFavorite(objectId, !isCurrentlyFavorite)
        }
    }

    // Загрузить из API
    private suspend fun loadFromApi() {
        withContext(Dispatchers.IO) {
            try {
                // Загружаем APOD (картинки дня)
                val apodResponse = nasaService.getAPOD(count = 20)
                if (apodResponse.isSuccessful) {
                    apodResponse.body()?.let { objects ->
                        // Сохраняем в БД
                        appExecutors.diskIO().execute {
                            dao.insertAll(objects)
                        }
                    }
                }

                // Можно добавить загрузку астероидов
                // val asteroids = nasaService.getAsteroids(getTodayDate())

            } catch (e: Exception) {
                // Просто игнорируем ошибки сети, работаем с локальными данными
            }
        }
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // Проверка сети
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as android.net.ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (e: Exception) {
            false
        }
    }
}