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

    private val nasaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(com.hfad.stars.api.NASAApiService::class.java)
    }

    // Просто возвращаем LiveData — ViewModel будет наблюдать за ним
    fun getAllObjects(): LiveData<List<CosmicObject>> = dao.getAllObjects()


    // Избранное
    fun getFavorites(): LiveData<List<CosmicObject>> = dao.getFavorites()

    // Новый метод для поиска
    fun searchObjects(query: String): LiveData<List<CosmicObject>> = dao.searchObjects(query)

    // Блокирующий запрос — нужен для сохранения isFavorite при обновлении
    suspend fun getAllObjectsBlocking(): List<CosmicObject> = withContext(Dispatchers.IO) {
        dao.getAllObjectsBlocking()
    }

    suspend fun refreshFromNetwork() = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable(appContext)) {
            Log.d("Repository", "Нет интернета — пропускаем обновление")
            return@withContext
        }

        try {
            Log.d("Repository", "Загружаем данные из NASA APOD...")
            val response = nasaApi.getAPOD(count = 20)

            if (response.isSuccessful && response.body() != null) {
                val apiObjects = response.body()!!

                // Сохраняем статус избранного
                val currentFavoritesMap = getAllObjectsBlocking()
                    .associateBy { it.id }

                val updatedObjects = apiObjects.map { apiObj ->
                    val existing = currentFavoritesMap[apiObj.id]
                    apiObj.copy(isFavorite = existing?.isFavorite ?: false)
                }

                dao.insertAll(updatedObjects)
                Log.d("Repository", "Успешно сохранено ${updatedObjects.size} объектов")
            } else {
                Log.e("Repository", "Ошибка API: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Исключение при загрузке", e)
        }
    }
    suspend fun setFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(id, isFavorite)
    }

    suspend fun getObjectById(id: String): CosmicObject? = withContext(Dispatchers.IO) {
        dao.getObjectById(id)
    }


    // Проверка сети
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}

