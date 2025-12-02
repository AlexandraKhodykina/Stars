package com.hfad.stars.repository
import android.content.Context
import androidx.lifecycle.LiveData
import com.hfad.stars.App
import com.hfad.stars.api.NASAApiService
import com.hfad.stars.database.AppDatabase
import com.hfad.stars.database.CosmicObjectDao
import com.hfad.stars.model.CosmicObject
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class CosmicRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).cosmicObjectDao()
    private val appContext = context.applicationContext

    private val _allObjects = MutableLiveData<List<CosmicObject>>()

    private val nasaApi: NASAApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NASAApiService::class.java)
    }

    // Получить все объекты
    fun getAllObjects(): LiveData<List<CosmicObject>> {
        // Сначала подписываемся на данные из БД
        dao.getAllObjects().observeForever { objects ->
            _allObjects.value = objects
        }
        return _allObjects
    }

    // Загрузить из сети и сохранить в БД
    fun refreshFromNetwork() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("CosmicRepository", "Загрузка данных из NASA API")
                val response = nasaApi.getAPOD()

                if (response.isSuccessful) {
                    response.body()?.let { objects ->
                        Log.d("CosmicRepository", "Получено ${objects.size} объектов из API")

                        // Получаем текущие объекты из БД для сохранения статуса избранного
                        val currentObjects = dao.getAllObjects().value ?: emptyList()

                        val objectsWithFavorites = objects.map { apiObject ->
                            // Находим соответствующий объект в БД
                            val existing = currentObjects.find { it.id == apiObject.id }
                            // Сохраняем статус избранного если объект уже есть в БД
                            apiObject.copy(isFavorite = existing?.isFavorite ?: false)
                        }

                        // Сохраняем в БД
                        dao.insertAll(objectsWithFavorites)
                        Log.d("CosmicRepository", "Данные сохранены в БД")
                    }
                } else {
                    Log.e("CosmicRepository", "Ошибка API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CosmicRepository", "Сетевая ошибка: ${e.message}", e)
            }
        }
    }

    // Избранное
    fun getFavorites(): LiveData<List<CosmicObject>> = dao.getFavorites()

    suspend fun toggleFavorite(id: String, currentStatus: Boolean) {
        dao.updateFavorite(id, !currentStatus)
    }

    suspend fun getObjectById(id: String): CosmicObject? {
        return dao.getObjectById(id)
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
