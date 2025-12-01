package com.hfad.stars.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hfad.stars.model.CosmicObject
import com.hfad.stars.repository.CosmicRepository
import kotlinx.coroutines.launch


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
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _networkAvailable.value = repository.isNetworkAvailable(getApplication())

            try {
                // Подписываемся на LiveData из репозитория
                repository.getAllObjects().observeForever { objects ->
                    _cosmicObjects.value = objects
                }
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadData()
            } else {
                repository.search(query).observeForever { objects ->
                    _cosmicObjects.value = objects
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
    // ДОБАВЛЕННЫЙ МЕТОД: Переключение избранного
    fun toggleFavorite(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            try {
                // Используем текущий статус isFavorite для переключения
                repository.toggleFavorite(cosmicObject.id, cosmicObject.isFavorite)
            } catch (e: Exception) {
                // Можно добавить обработку ошибок, если нужно
                e.printStackTrace()
            }
        }
    }

}
























