
package com.example.hava.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CityViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow<CityUiState>(CityUiState.Loading)

    val uiState: StateFlow<CityUiState> =
        _uiState.asStateFlow()

    private val _weatherState =
        MutableStateFlow<WeatherResponse?>(null)

    val weatherState: StateFlow<WeatherResponse?> =
        _weatherState.asStateFlow()

    private val _weatherError =
        MutableStateFlow<String?>(null)

    val weatherError: StateFlow<String?> =
        _weatherError.asStateFlow()

    fun searchCities(name: String) {
        if (name.isBlank()) {
            _uiState.value = CityUiState.Empty
            return
        }

        _uiState.value = CityUiState.Loading

        viewModelScope.launch {
            try {
                val response = ApiClient.cityApi.searchCities(name)
                val cities = response.results.orEmpty()

                _uiState.value = if (cities.isEmpty()) {
                    CityUiState.Empty
                } else {
                    CityUiState.Success(cities)
                }
            } catch (e: Exception) {
                _uiState.value = CityUiState.Error(
                    e.message ?: "Bağlantı hatası oluştu."
                )
            }
        }
    }

    fun getWeather(city: City) {
        _weatherError.value = null
        _weatherState.value = null

        viewModelScope.launch {
            try {
                _weatherState.value =
                    ApiClient.weatherApi.getCurrentWeather(
                        latitude = city.latitude,
                        longitude = city.longitude
                    )
            } catch (e: Exception) {
                _weatherError.value =
                    e.message ?: "Hava durumu alınamadı."
            }
        }
    }
}
