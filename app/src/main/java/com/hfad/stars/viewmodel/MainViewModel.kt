package com.hfad.stars.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hfad.stars.model.CosmicObject
import com.hfad.stars.repository.CosmicRepository
import kotlinx.coroutines.launch
import android.util.Log

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CosmicRepository(application)

    private val _cosmicObjects = MutableLiveData<List<CosmicObject>>()
    val cosmicObjects: LiveData<List<CosmicObject>> = _cosmicObjects

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _networkAvailable = MutableLiveData<Boolean>()
    val networkAvailable: LiveData<Boolean> = _networkAvailable

    init {
        Log.d("MainViewModel", "ViewModel создан")
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            Log.d("MainViewModel", "Начало загрузки данных")
            _isLoading.value = true
            _error.value = ""

            try {
                val isNetworkAvailable = repository.isNetworkAvailable(getApplication())
                _networkAvailable.value = isNetworkAvailable

                // Получаем данные из репозитория
                repository.getAllObjects().observeForever { objects ->
                    _cosmicObjects.value = objects

                    if (objects.isEmpty() && isNetworkAvailable) {
                        _error.value = "Нет данных для отображения"
                    }
                }

                // Загружаем обновления из сети если есть соединение
                if (isNetworkAvailable) {
                    repository.refreshFromNetwork()
                }

            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка загрузки: ${e.message}", e)
                _error.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        // Получаем текущий список из LiveData
        val currentList = _cosmicObjects.value ?: emptyList()

        if (query.isEmpty()) {
            // Если запрос пустой, показываем все объекты
            // Для этого нужно перезагрузить данные
            loadData()
        } else {
            // Фильтруем по запросу
            val filtered = currentList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
            }
            _cosmicObjects.value = filtered
        }
    }


    fun toggleFavorite(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Переключение избранного для: ${cosmicObject.name}")

                // Получаем актуальный объект из БД
                val currentObject = repository.getObjectById(cosmicObject.id)

                currentObject?.let { obj ->
                    Log.d("MainViewModel", "Текущий статус isFavorite: ${obj.isFavorite}")

                    // ВАЖНО: Передаем id и текущий статус!
                    repository.toggleFavorite(cosmicObject.id, obj.isFavorite)

                    // Обновляем в локальном списке
                    updateItemInList(cosmicObject.id, !obj.isFavorite)

                    Log.d("MainViewModel", "Новый статус isFavorite: ${!obj.isFavorite}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка переключения избранного", e)
                _error.value = "Не удалось обновить избранное"
            }
        }
    }



    private fun updateItemInList(id: String, isFavorite: Boolean) {
        val currentList = _cosmicObjects.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedObject = currentList[index].copy(isFavorite = isFavorite)
            currentList[index] = updatedObject
            _cosmicObjects.value = currentList
        }
    }

    fun refresh() {
        Log.d("MainViewModel", "Принудительное обновление данных")
        loadData()
    }

}

