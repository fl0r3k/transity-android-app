package pl.transity.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.maps.android.SphericalUtil
import pl.transity.app.AppExecutors
import pl.transity.app.data.database.FavoriteLineDao
import pl.transity.app.data.model.FavoriteLine
import pl.transity.app.data.model.StopArrival
import pl.transity.app.data.model.Vehicle
import pl.transity.app.data.network.Bounds
import pl.transity.app.data.network.vehicles.VehicleNetworkDataSource

class VehicleRepository(
        private val vehicleNetworkDataSource: VehicleNetworkDataSource,
        private val favoriteLineDao: FavoriteLineDao,
        private val executors: AppExecutors
) {

    val vehicles = vehicleNetworkDataSource.getFetchedVehicles()
    val favoriteLinesFlowable = favoriteLineDao.getFavoriteLines()
    val favoriteLines = favoriteLineDao.getFavoriteLinesLiveData()

    val vehicle: LiveData<Vehicle> = Transformations.map(vehicleNetworkDataSource.getFetchedSelectedVehicle()) {
        for (stop in it.stops) {
            stop.distance = SphericalUtil.computeDistanceBetween(it.position, stop.position)
        }
        stopsArrivals.postValue(it.stops)
        it
    }

    private val favoriteLinesSubscription = favoriteLinesFlowable.subscribe { favoriteLines ->
        vehicleNetworkDataSource.setFavoriteLines(favoriteLines)
    }

    val stopsArrivals = MutableLiveData<List<StopArrival>>().default(emptyList())

    companion object {
        private val TAG = VehicleRepository::class.simpleName
        private var INSTANCE: VehicleRepository? = null

        fun getInstance(vehicleNetworkDataSource: VehicleNetworkDataSource, favoriteLineDao: FavoriteLineDao, executors: AppExecutors): VehicleRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: VehicleRepository(vehicleNetworkDataSource, favoriteLineDao, executors).also { INSTANCE = it }
                }
    }

    fun startVehiclesFetcher() {
        vehicleNetworkDataSource.startFetcher()
    }

    fun stopVehiclesFetcher() {
        vehicleNetworkDataSource.stopFetcher()
    }

    fun setVehiclesFetchRegion(bounds: Bounds) {
        vehicleNetworkDataSource.fetchBounds = bounds
    }

    fun addLineToFavorites(line: String) {
        Log.d(TAG, "Adding line $line to favorites")
        executors.diskIO.execute { favoriteLineDao.addLine(FavoriteLine(line)) }
    }

    fun removeLineFromFavorites(line: String) {
        Log.d(TAG, "Removing line $line from favorites")
        executors.diskIO.execute { favoriteLineDao.removeLine(line) }
    }

    fun setOnlyFavoriteVehicles(onlyFavorite: Boolean) {
        vehicleNetworkDataSource.onlyFavorite = onlyFavorite
    }

    fun fetchSelectedVehicleDetails(id: String) {
        vehicleNetworkDataSource.fetchSelectedVehicleDetails(id)
    }
}

private fun <T> MutableLiveData<T>.default(initialValue: T): MutableLiveData<T> = apply { value = initialValue }