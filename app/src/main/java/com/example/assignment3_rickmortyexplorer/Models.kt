package com.example.assignment3_rickmortyexplorer



import com.google.gson.annotations.SerializedName

// ---------- Categorias ----------
enum class Category(val label: String, val plural: String) {
    CHARACTER("Character", "Characters"),
    EPISODE("Episode", "Episodes"),
    LOCATION("Location", "Locations")
}

// ---------- Estrutura comum: "info" tem o total de resultados ----------
data class ApiInfo(val count: Int, val pages: Int)

// ---------- Character ----------
data class CharacterResponse(val info: ApiInfo, val results: List<CharacterDto>)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String
)

// ---------- Episode ----------
data class EpisodeResponse(val info: ApiInfo, val results: List<EpisodeDto>)
data class EpisodeDto(
    val id: Int,
    val name: String,
    @SerializedName("air_date") val airDate: String,
    val episode: String,
    val characters: List<String>
)

// ---------- Location ----------
data class LocationResponse(val info: ApiInfo, val results: List<LocationDto>)
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>
)

// ---------- Modelo único que o ecrã mostra (igual para as 3 categorias) ----------
data class DisplayItem(
    val name: String,
    val imageUrl: String?,
    val label1: String,
    val value1: String,
    val label2: String,
    val value2: String
)

// Episódios e localizações não têm imagem: usa o avatar da 1.ª personagem da lista
fun avatarFromUrls(urls: List<String>): String? {
    val id = urls.firstOrNull()?.substringAfterLast("/")?.toIntOrNull() ?: return null
    return "https://rickandmortyapi.com/api/character/avatar/$id.jpeg"
}

// ---------- Conversões para DisplayItem ----------
fun CharacterDto.toDisplayItem() = DisplayItem(
    name = name, imageUrl = image,
    label1 = "Status", value1 = status,
    label2 = "Species", value2 = species
)

fun EpisodeDto.toDisplayItem() = DisplayItem(
    name = name, imageUrl = avatarFromUrls(characters),
    label1 = "Air date", value1 = airDate,
    label2 = "Episode", value2 = episode
)

fun LocationDto.toDisplayItem() = DisplayItem(
    name = name, imageUrl = avatarFromUrls(residents),
    label1 = "Type", value1 = type,
    label2 = "Dimension", value2 = dimension
)