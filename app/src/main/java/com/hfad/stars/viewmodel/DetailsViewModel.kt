package com.hfad.stars.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hfad.stars.model.CosmicObject
import com.hfad.stars.repository.CosmicRepository
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CosmicRepository(application)

    private val _cosmicObject = MutableLiveData<CosmicObject?>()
    val cosmicObject: LiveData<CosmicObject?> = _cosmicObject

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadObject(objectId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val obj = repository.getObjectById(objectId)  // ← исправлено
                _cosmicObject.value = obj
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить объект"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentObject = _cosmicObject.value ?: return@launch
            repository.toggleFavorite(currentObject.id, currentObject.isFavorite)

            // Обновляем объект после изменения
            loadObject(currentObject.id)
        }
    }
}