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

    fun removeFromFavorites(cosmicObject: CosmicObject) {
        viewModelScope.launch {
            repository.setFavorite(cosmicObject.id, false)
        }
    }

}