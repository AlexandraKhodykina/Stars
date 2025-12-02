package com.hfad.stars.api
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.hfad.stars.model.CosmicObject


interface NASAApiService {

    // Astronomy Picture of the Day
    @GET("planetary/apod")
    suspend fun getAPOD(
        @Query("api_key") apiKey: String = "yZGDPznGxJMuDfAj2eUb7v0GocJcLfeZnljG8OFQ",
        @Query("count") count: Int = 20
    ): Response<List<CosmicObject>>

    // Near Earth Objects
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("api_key") apiKey: String = "DEMO_KEY",
        @Query("start_date") startDate: String
    ): Response<AsteroidResponse>
}

// Простой DTO для астероидов
data class AsteroidResponse(
    val near_earth_objects: Map<String, List<Asteroid>>
)

data class Asteroid(
    val id: String,
    val name: String,
    val estimated_diameter: Map<String, Map<String, Double>>,
    val close_approach_data: List<CloseApproach>
)

data class CloseApproach(
    val miss_distance: Map<String, String>
)