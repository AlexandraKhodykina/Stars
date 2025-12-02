package com.hfad.stars.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//Простая модель космического объекта
// Используется и для API, и для базы данных
@Entity(tableName = "cosmic_objects")
data class CosmicObject(
    @PrimaryKey
    @SerializedName("date")
    val id: String = "",

    @SerializedName("title")
    val name: String = "",

    @SerializedName("explanation")
    val description: String? = null,

    @SerializedName("url")
    val imageUrl: String? = null,

    @SerializedName("media_type")
    val type: String? = null,

    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,

    // ИСПРАВЛЕНО: hdurl (маленькими буквами) как в JSON
    @SerializedName("hdurl")
    val hdUrl: String? = null,

    @SerializedName("copyright")
    val copyright: String? = null,

    val isFavorite: Boolean = false
)