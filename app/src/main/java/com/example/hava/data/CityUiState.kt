
package com.example.hava.data

sealed interface CityUiState {
    data object Loading : CityUiState

    data class Success(
        val cities: List<City>
    ) : CityUiState

    data object Empty : CityUiState

    data class Error(
        val message: String
    ) : CityUiState
}
