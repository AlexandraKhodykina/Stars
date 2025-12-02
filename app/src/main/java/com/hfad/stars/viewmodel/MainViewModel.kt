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

    private val _isLoading = androidx.lifecycle.MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = androidx.lifecycle.MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        refresh() // Первая загрузка при создании
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""
            try {
                repository.refreshFromNetwork()
                // После обновления показываем все объекты
                repository.getAllObjects().observeForever { list ->
                    _cosmicObjects.value = list
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun toggleFavorite(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            val newStatus = !cosmicObject.isFavorite
            repository.setFavorite(cosmicObject.id, newStatus)
        }
    }

    // Обновлённый метод поиска
    fun search(query: String) {
        if (query.isEmpty()) {
            repository.getAllObjects().observeForever { list ->
                _cosmicObjects.value = list
            }
        } else {
            repository.searchObjects(query).observeForever { list ->
                _cosmicObjects.value = list
            }
        }
    }

}

