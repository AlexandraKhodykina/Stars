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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class CosmicRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).cosmicObjectDao()

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
                    // Загружаем только APOD для простоты
                    val response = nasaService.getAPOD(count = 20)
                    if (response.isSuccessful) {
                        response.body()?.let { objects ->
                            dao.insertAll(objects)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
    // Проверка сети
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    )
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
}

































