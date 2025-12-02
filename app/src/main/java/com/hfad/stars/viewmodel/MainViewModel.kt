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
                // Проверяем доступность сети
                val isNetworkAvailable = repository.isNetworkAvailable(getApplication())
                _networkAvailable.value = isNetworkAvailable
                Log.d("MainViewModel", "Сеть доступна: $isNetworkAvailable")

                // Загружаем данные (репозиторий сам решит, откуда брать)
                repository.getAllObjects().observeForever { objects ->
                    _cosmicObjects.value = objects
                    Log.d("MainViewModel", "Получено ${objects.size} объектов")

                    if (objects.isEmpty()) {
                        if (isNetworkAvailable) {
                            _error.value = "Нет данных для отображения"
                        } else {
                            _error.value = "Нет подключения к интернету"
                        }
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
                Log.d("MainViewModel", "Загрузка завершена")
            }
        }
    }

    fun toggleFavorite(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Переключение избранного для: ${cosmicObject.name}")

                // Получаем актуальный объект из БД, чтобы узнать текущий статус
                val currentObject = repository.getObjectById(cosmicObject.id)

                currentObject?.let { obj ->
                    Log.d("MainViewModel", "Текущий статус isFavorite: ${obj.isFavorite}")

                    // Инвертируем статус
                    val newFavoriteStatus = !obj.isFavorite
                    repository.toggleFavorite(cosmicObject.id, obj.isFavorite)

                    Log.d("MainViewModel", "Новый статус isFavorite: $newFavoriteStatus")

                    // Обновляем локальный список
                    updateItemInList(cosmicObject.id, newFavoriteStatus)
                } ?: run {
                    Log.w("MainViewModel", "Объект не найден в БД: ${cosmicObject.id}")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Ошибка переключения избранного", e)
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

    fun search(query: String) {
        // Простой поиск по уже загруженным данным
        val currentList = repository.getAllObjects().value ?: emptyList()
        if (query.isEmpty()) {
            _cosmicObjects.value = currentList
        } else {
            val filtered = currentList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
            }
            _cosmicObjects.value = filtered
        }
    }
}





















