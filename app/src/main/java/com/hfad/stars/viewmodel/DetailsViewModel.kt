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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadObject(objectId: String) {
        viewModelScope.launch {
            try {
                val obj = repository.getObjectById(objectId)
                _cosmicObject.value = obj
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить объект"
            }
        }
    }

    // ИСПРАВЛЕНО: теперь используем setFavorite вместо toggleFavorite
    fun toggleFavorite() {
        viewModelScope.launch {
            val current = _cosmicObject.value ?: return@launch
            val newStatus = !current.isFavorite
            repository.setFavorite(current.id, newStatus)

            // Обновляем объект, чтобы UI сразу увидело изменения
            _cosmicObject.value = current.copy(isFavorite = newStatus)
        }
    }

}
