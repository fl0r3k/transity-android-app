package pl.transity.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.transity.app.data.StopRepository
import pl.transity.app.data.VehicleRepository

class MainActivityViewModelFactory(
        private val stopRepository: StopRepository,
        private val vehicleRepository: VehicleRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(stopRepository, vehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}