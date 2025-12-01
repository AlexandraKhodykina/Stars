package com.hfad.stars.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//Простая модель космического объекта
// Используется и для API, и для базы данных
@Entity(tableName = "cosmic_objects")
data class CosmicObject(
    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val name: String,

    @SerializedName("explanation")
    val description: String?,

    @SerializedName("url")
    val imageUrl: String?,

    @SerializedName("media_type")
    val type: String? = null,

    @SerializedName("date")
    val date: String? = null,

    val isFavorite: Boolean = false,

    val distance: String? = null,

    val size: String? = null
)