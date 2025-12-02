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
    val id: String = "",

    @SerializedName("title")
    val name: String = "",

    @SerializedName("explanation")
    val description: String? = null,

    @SerializedName("url")
    val imageUrl: String? = null,

    @SerializedName("media_type")
    val type: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,

    @SerializedName("hdurl")
    val hdUrl: String? = null,

    val isFavorite: Boolean = false
)