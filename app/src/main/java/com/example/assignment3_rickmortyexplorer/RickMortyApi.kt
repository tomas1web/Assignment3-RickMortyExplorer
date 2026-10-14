package com.example.assignment3_rickmortyexplorer



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://rickandmortyapi.com/api/"

interface RickMortyApiService {
    // name = null -> o Retrofit não envia o parâmetro (sem filtro)
    @GET("character")
    suspend fun getCharacters(@Query("name") name: String?): CharacterResponse

    @GET("episode")
    suspend fun getEpisodes(@Query("name") name: String?): EpisodeResponse

    @GET("location")
    suspend fun getLocations(@Query("name") name: String?): LocationResponse
}

object RickMortyApi {
    val retrofitService: RickMortyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickMortyApiService::class.java)
    }
}