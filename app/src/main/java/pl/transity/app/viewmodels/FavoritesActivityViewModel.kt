package pl.transity.app.viewmodels

import androidx.lifecycle.ViewModel
import pl.transity.app.data.StopRepository
import pl.transity.app.data.VehicleRepository

class FavoritesActivityViewModel(
        private val stopRepository: StopRepository,
        private val vehicleRepository: VehicleRepository
) : ViewModel() {

    val favoriteStops = stopRepository.favoriteStops
    val favoriteLines = vehicleRepository.favoriteLines
}