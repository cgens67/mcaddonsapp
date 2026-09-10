package com.cgens67.mcaddons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    object Downloaded : DownloadState()
}

data class AddonUiState(
    val addons: List<AddonItem> = emptyList(),
    val filteredAddons: List<AddonItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val downloadStates: Map<Long, DownloadState> = emptyMap(),
    val selectedCategory: String = "All",
    val searchQuery: String = ""
)

class AddonViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AddonUiState())
    val uiState: StateFlow<AddonUiState> = _uiState.asStateFlow()

    init {
        fetchAddons()
    }

    fun fetchAddons(isUserRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = if (isUserRefresh) false else it.addons.isEmpty(),
                    isRefreshing = isUserRefresh,
                    error = null
                ) 
            }
            try {
                val url = "${SupabaseConfig.PROJECT_URL}/rest/v1/addons?select=*"
                val response = SupabaseConfig.client.get(url)
                val items: List<AddonItem> = response.body()
                _uiState.update { 
                    it.copy(
                        addons = items, 
                        isLoading = false,
                        isRefreshing = false,
                        filteredAddons = filterItems(items, it.searchQuery, it.selectedCategory)
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isRefreshing = false, 
                        error = e.localizedMessage
                    ) 
                }
            }
        }
    }

    fun refreshAddons() {
        fetchAddons(isUserRefresh = true)
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { 
            it.copy(
                searchQuery = query,
                filteredAddons = filterItems(it.addons, query, it.selectedCategory)
            ) 
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { 
            it.copy(
                selectedCategory = category,
                filteredAddons = filterItems(it.addons, it.searchQuery, category)
            ) 
        }
    }

    private fun filterItems(items: List<AddonItem>, query: String, category: String): List<AddonItem> {
        return items.filter { item ->
            val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() || item.title.contains(query, ignoreCase = true) || item.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    fun downloadAndInstall(addon: AddonItem) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    downloadStates = it.downloadStates.toMutableMap().apply { put(addon.id, DownloadState.Downloading) }
                ) 
            }
            
            val success = MinecraftInstaller.downloadAndInstall(getApplication(), addon)
            
            _uiState.update { 
                it.copy(
                    downloadStates = it.downloadStates.toMutableMap().apply { 
                        put(addon.id, if (success) DownloadState.Downloaded else DownloadState.Idle) 
                    }
                ) 
            }
        }
    }
}
