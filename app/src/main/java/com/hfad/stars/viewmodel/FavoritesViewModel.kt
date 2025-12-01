package com.hfad.stars.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hfad.stars.model.CosmicObject
import com.hfad.stars.repository.CosmicRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CosmicRepository(application)

    val favorites: LiveData<List<CosmicObject>> = repository.getFavorites()

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    init {
        // Наблюдаем за изменением избранных
        favorites.observeForever { objects ->
            _isEmpty.value = objects.isEmpty()
        }
    }
    // Функция удаления объекта
    fun deleteFavorite(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            try {
                // Можно просто снять отметку "избранное"
                repository.toggleFavorite(cosmicObject.id, true)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                _deleteSuccess.value = false
            }
        }
    }

}