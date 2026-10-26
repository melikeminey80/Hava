
package com.example.hava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hava.data.City
import com.example.hava.data.CityUiState
import com.example.hava.data.CityViewModel
import com.example.hava.ui.theme.HavaTheme

class MainActivity : ComponentActivity() {

    private val cityViewModel: CityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HavaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    CitySearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = cityViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun CitySearchScreen(
    modifier: Modifier = Modifier,
    viewModel: CityViewModel
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var selectedCity by rememberSaveable { mutableStateOf("") }


    val popularCities = listOf(
        "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya",
        "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
        "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur",
        "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
        "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum",
        "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
        "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir",
        "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
        "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa",
        "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
        "Niğde", "Ordu", "Rize", "Sakarya", "Samsun",
        "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
        "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van",
        "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
        "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan",
        "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye",
        "Düzce"
    )


    val filteredCities = if (searchText.isBlank()) {
        popularCities
    } else {
        popularCities.filter {
            it.contains(searchText.trim(), ignoreCase = true)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()
    val weatherError by viewModel.weatherError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Şehir Ara",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Türkiye'deki şehirleri keşfet",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                selectedCity = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Şehir adı") },
            placeholder = { Text("Örn. Ankara") },
            singleLine = true,
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Text(
                        text = "Sil",
                        modifier = Modifier
                            .clickable {
                                searchText = ""
                                selectedCity = ""
                            }
                            .padding(8.dp)
                    )
                }
            }
        )

        if (selectedCity.isBlank()) {
            Text(
                text = if (searchText.isBlank()) {
                    "Popüler şehirler"
                } else {
                    "Eşleşen şehirler"
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 12.dp)
            )

            if (filteredCities.isEmpty()) {
                Text(
                    text = "Eşleşen şehir bulunamadı.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(filteredCities) { city ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    searchText = city
                                    selectedCity = city
                                    viewModel.searchCities(city)
                                },
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Text(
                                text = city,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        if (selectedCity.isNotBlank()) {
            when (val state = uiState) {

                is CityUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Şehirler aranıyor...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                is CityUiState.Success -> {
                    Text(
                        text = "Şehir sonuçları",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(vertical = 12.dp)
                    )

                    CityResults(
                        cities = state.cities,
                        onCityClick = { city ->
                            searchText = city.name
                            selectedCity = city.name
                            viewModel.getWeather(city)
                        }
                    )
                }

                is CityUiState.Empty -> {
                    Text(
                        text = "API üzerinden şehir bilgisi bulunamadı.",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                is CityUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bağlantı hatası!",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = state.message,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.searchCities(searchText)
                            }
                        ) {
                            Text("Tekrar dene")
                        }
                    }
                }
            }
        }

        weatherState?.let { weather ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🌤️ Güncel Hava Durumu",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text("🌡️ Sıcaklık: ${weather.current.temperature} °C")
                    Text("🤗 Hissedilen: ${weather.current.apparentTemperature} °C")
                    Text("💧 Nem: %${weather.current.humidity}")
                    Text("💨 Rüzgâr: ${weather.current.windSpeed} km/sa")
                    Text("Hava durumu kodu: ${weather.current.weatherCode}")
                }
            }
        }

        weatherError?.let { error ->
            Text(
                text = "Hava durumu alınamadı: $error",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CityResults(
    cities: List<City>,
    onCityClick: (City) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 280.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cities) { city ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCityClick(city)
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = city.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = listOfNotNull(
                                city.admin1,
                                city.country
                            ).joinToString(", ")
                        )

                        Text(
                            text = "Enlem: ${city.latitude}, Boylam: ${city.longitude}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Şehir detayları"
                    )
                }
            }
        }
    }
}
