package pl.transity.app.utilities

import android.content.Context
import pl.transity.app.AppExecutors
import pl.transity.app.data.StopRepository
import pl.transity.app.data.VehicleRepository
import pl.transity.app.data.database.AppDatabase
import pl.transity.app.data.network.stops.StopNetworkDataSource
import pl.transity.app.data.network.stops.StopService
import pl.transity.app.data.network.stops.StopsGroupNetworkDataSource
import pl.transity.app.data.network.stops.StopsGroupService
import pl.transity.app.data.network.vehicles.VehicleNetworkDataSource
import pl.transity.app.data.network.vehicles.VehicleService
import pl.transity.app.viewmodels.FavoritesActivityViewModelFactory
import pl.transity.app.viewmodels.MainActivityViewModelFactory


/**
 * Provides static methods to inject the various classes needed
 */
object Injection {

    fun provideMainActivityViewModelFactory(context: Context): MainActivityViewModelFactory {
        val stopRepository = provideStopRepository(context)
        val vehicleRepository = provideVehicleRepository(context)
        return MainActivityViewModelFactory(stopRepository, vehicleRepository)
    }

    fun provideFavoritesActivityViewModelFactory(context: Context): FavoritesActivityViewModelFactory {
        val stopRepository = provideStopRepository(context)
        val vehicleRepository = provideVehicleRepository(context)
        return FavoritesActivityViewModelFactory(stopRepository, vehicleRepository)
    }

    private fun provideStopRepository(context: Context): StopRepository {
        val stopDao = provideAppDatabase(context).stopDao()
        val stopsGroupDao = provideAppDatabase(context).stopsGroupDao()
        val favoriteStopDao = provideAppDatabase(context).favoriteStopDao()
        val fetchTimestampDao = provideAppDatabase(context).fetchTimestampDao()
        val stopNetworkDataSource = provideStopNetworkDataSource(context)
        val stopsGroupNetworkDataSource = provideStopsGroupNetworkDataSource(context)
        val executors = provideAppExecutors(context)
        return StopRepository.getInstance(stopDao, stopsGroupDao, favoriteStopDao, fetchTimestampDao, stopNetworkDataSource, stopsGroupNetworkDataSource, executors)
    }

    private fun provideVehicleRepository(context: Context): VehicleRepository {
        val vehicleNetworkDataSource = provideVehicleNetworkDataSource(context)
        val favouriteLineDao = provideAppDatabase(context).favouriteLineDao()
        val executors = provideAppExecutors(context)
        return VehicleRepository.getInstance(vehicleNetworkDataSource, favouriteLineDao, executors)
    }

    private fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    private fun provideStopNetworkDataSource(context: Context): StopNetworkDataSource {
        val stopApi = provideStopService().stopApi
        val executors = provideAppExecutors(context)
        return StopNetworkDataSource.getInstance(stopApi, executors)
    }

    private fun provideStopsGroupNetworkDataSource(context: Context): StopsGroupNetworkDataSource {
        val stopsGroupApi = provideStopsGroupService().stopsGroupApi
        val executors = provideAppExecutors(context)
        return StopsGroupNetworkDataSource.getInstance(context, stopsGroupApi, executors)
    }

    private fun provideVehicleNetworkDataSource(context: Context): VehicleNetworkDataSource {
        val vehicleApi = provideVehicleService().vehicleApi
        val executors = provideAppExecutors(context)
        return VehicleNetworkDataSource.getInstance(vehicleApi, executors)
    }

    private fun provideStopService(): StopService {
        return StopService.getInstance()
    }

    fun provideAppExecutors(context: Context): AppExecutors {
        return AppExecutors.getInstance(context)
    }

    private fun provideStopsGroupService(): StopsGroupService {
        return StopsGroupService.getInstance()
    }

    private fun provideVehicleService(): VehicleService {
        return VehicleService.getInstance()
    }
}
