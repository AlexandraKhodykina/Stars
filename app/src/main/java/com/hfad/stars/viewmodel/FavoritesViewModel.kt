package com.hfad.stars.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hfad.stars.model.CosmicObject
import com.hfad.stars.repository.CosmicRepository


class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CosmicRepository(application)

    val favorites: LiveData<List<CosmicObject>> = repository.getFavorites()

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        // Наблюдаем за изменением избранных
        favorites.observeForever { objects ->
            _isEmpty.value = objects.isEmpty()
        }
    }
}