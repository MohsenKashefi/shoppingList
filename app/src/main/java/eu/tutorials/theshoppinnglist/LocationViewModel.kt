package eu.tutorials.myshoppinglistapp


class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = mutableStateOf<List<GeocodingResult>>(emptyList())
    val address: State<List<GeocodingResult>> = _address

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun updateLocation(newLocation: LocationData) {
        _location.value = newLocation
    }

    fun fetchAddress(latlng: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = RetrofitClient.create().getAddressFromCoordinates(latlng, YOUR_API_KEY)
                _address.value = result.results
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }
}
