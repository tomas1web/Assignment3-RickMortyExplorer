package com.example.assignment3_rickmortyexplorer



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ExplorerUiState(
    val category: Category = Category.CHARACTER,
    val filter: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val total: Int = 0,                         // total na API (info.count)
    val items: List<DisplayItem> = emptyList(), // itens recebidos nesta pesquisa
    val current: DisplayItem? = null            // item mostrado
)

class ExplorerViewModel : ViewModel() {

    var uiState by mutableStateOf(ExplorerUiState())
        private set

    private var searchJob: Job? = null

    // Ao abrir a app, carrega e mostra um item aleatório
    init { search() }

    fun onCategorySelected(category: Category) {
        uiState = uiState.copy(category = category, filter = "")
        search()
    }

    fun onFilterChanged(text: String) {
        uiState = uiState.copy(filter = text)
        search(debounce = true)
    }

    fun roll() {
        val items = uiState.items
        if (items.isNotEmpty()) uiState = uiState.copy(current = items.random())
    }

    private fun search(debounce: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (debounce) delay(400) // espera o utilizador parar de escrever
            val category = uiState.category
            val name = uiState.filter.trim().ifEmpty { null }
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                val api = RickMortyApi.retrofitService
                // O pedido muda conforme a categoria escolhida
                val (total, items) = when (category) {
                    Category.CHARACTER -> {
                        val r = api.getCharacters(name)
                        r.info.count to r.results.map { it.toDisplayItem() }
                    }
                    Category.EPISODE -> {
                        val r = api.getEpisodes(name)
                        r.info.count to r.results.map { it.toDisplayItem() }
                    }
                    Category.LOCATION -> {
                        val r = api.getLocations(name)
                        r.info.count to r.results.map { it.toDisplayItem() }
                    }
                }
                uiState = uiState.copy(
                    isLoading = false,
                    total = total,
                    items = items,
                    current = items.randomOrNull()
                )
            } catch (e: HttpException) {
                // A API responde 404 quando o filtro não encontra nada
                uiState = if (e.code() == 404) {
                    uiState.copy(isLoading = false, total = 0, items = emptyList(), current = null)
                } else {
                    uiState.copy(isLoading = false, error = "Server error (${e.code()})")
                }
            } catch (e: IOException) {
                uiState = uiState.copy(isLoading = false, error = "No internet connection")
            }
        }
    }
}