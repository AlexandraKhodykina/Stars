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

    // Прямой LiveData из репозитория — всё само обновляется!
    val cosmicObjects: LiveData<List<CosmicObject>> = repository.getAllObjects()

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

    // Поиск можно реализовать через отдельный запрос или Flow (пока заглушка)
    fun search(query: String) {
        // Пока просто обновляем — потом сделаем настоящий поиск
        refresh()
    }
}

