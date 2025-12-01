package com.hfad.stars.database
import androidx.lifecycle.LiveData
import androidx.room.*
import com.hfad.stars.model.CosmicObject

@Dao
interface CosmicObjectDao {
    // Получить все объекты (не избранные)
    @Query("SELECT * FROM cosmic_objects WHERE isFavorite = 0")
    fun getAllObjects(): LiveData<List<CosmicObject>>

    // Получить избранные
    @Query("SELECT * FROM cosmic_objects WHERE isFavorite = 1")
    fun getFavorites(): LiveData<List<CosmicObject>>

    // Поиск
    @Query("SELECT * FROM cosmic_objects WHERE title LIKE '%' || :query || '%' OR explanation LIKE '%' || :query || '%'")
    fun searchObjects(query: String): LiveData<List<CosmicObject>>

    // Получить по ID
    @Query("SELECT * FROM cosmic_objects WHERE id = :id")
    suspend fun getObjectById(id: String): CosmicObject?

    // Добавить или обновить
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(object: CosmicObject)

    // Добавить несколько
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(objects: List<CosmicObject>)

    // Обновить избранное
    @Query("UPDATE cosmic_objects SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    // Удалить
    @Delete
    suspend fun delete(object: CosmicObject)
}