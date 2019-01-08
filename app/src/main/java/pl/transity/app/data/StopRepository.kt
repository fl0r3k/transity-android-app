package pl.transity.app.data

import android.util.Log
import androidx.lifecycle.LiveData
import pl.transity.app.AppExecutors
import pl.transity.app.data.database.FavoriteStopDao
import pl.transity.app.data.database.FetchTimestampDao
import pl.transity.app.data.database.StopDao
import pl.transity.app.data.database.StopsGroupDao
import pl.transity.app.data.model.*
import pl.transity.app.data.network.stops.StopNetworkDataSource
import pl.transity.app.data.network.stops.StopsGroupNetworkDataSource
import java.sql.Timestamp


class StopRepository(
        private val stopDao: StopDao,
        private val stopsGroupDao: StopsGroupDao,
        private val favoriteStopDao: FavoriteStopDao,
        private val fetchTimestampDao: FetchTimestampDao,
        private val stopNetworkDataSource: StopNetworkDataSource,
        private val stopsGroupNetworkDataSource: StopsGroupNetworkDataSource,
        private val executors: AppExecutors) {

    companion object {
        private val TAG = StopRepository::class.simpleName
        private var INSTANCE: StopRepository? = null

        fun getInstance(stopDao: StopDao, stopsGroupDao: StopsGroupDao, favoriteStopDao: FavoriteStopDao, fetchTimestampDao: FetchTimestampDao, stopNetworkDataSource: StopNetworkDataSource, stopsGroupNetworkDataSource: StopsGroupNetworkDataSource, executors: AppExecutors): StopRepository =
                INSTANCE ?: synchronized(this) {
                    Log.d(TAG, "Creating or Returning Instance")
                    INSTANCE
                            ?: StopRepository(stopDao, stopsGroupDao, favoriteStopDao, fetchTimestampDao, stopNetworkDataSource, stopsGroupNetworkDataSource, executors).also { INSTANCE = it }
                }
    }

    private val networkDataStops = stopNetworkDataSource.getFetchedStops()
    private val networkDataStopsGroups = stopsGroupNetworkDataSource.getFetchedStopsGroups()

    val stops = stopDao.getStops()
    val favoriteStopsIds = favoriteStopDao.getFavoriteStopsIds()
    val favoriteStops = stopDao.getFavoriteStops()

    val stopLines: LiveData<List<StopLine>> = stopNetworkDataSource.getFetchedStopLines()
    val arrivals: LiveData<List<Arrival>> = stopNetworkDataSource.getFetchedArrivals()

    init {
        Log.d(TAG, "Initializing")

        networkDataStops.observeForever { newStopsFromNetwork ->
            executors.diskIO.execute {
                if (newStopsFromNetwork!!.isNotEmpty()) {
                    Log.d(TAG, "Inserting ${newStopsFromNetwork.size} stops to database")
                    Log.d(TAG, "Deleting old stops")
                    stopDao.deleteAll()
                    Log.d(TAG, "Old stops deleted")
                    Log.d(TAG, "Inserting new stops to database")
                    stopDao.bulkInsert(newStopsFromNetwork)
                    Log.d(TAG, "New stops inserted to database")
                }
            }
        }

        networkDataStopsGroups.observeForever { newStopsGroupsFromNetwork ->
            executors.diskIO.execute {
                if (newStopsGroupsFromNetwork!!.isNotEmpty()) {
                    Log.d(TAG, "Inserting ${newStopsGroupsFromNetwork.size} stops groups to database")
                    Log.d(TAG, "Deleting old stops groups")
                    stopsGroupDao.deleteAll()
                    Log.d(TAG, "Old stops groups deleted")
                    Log.d(TAG, "Inserting new stops groups to database")
                    stopsGroupDao.bulkInsert(newStopsGroupsFromNetwork)
                    Log.d(TAG, "New stops groups inserted to database")
                }
            }
        }

        executors.diskIO.execute {
            val stopsLastFetchTimestamp = fetchTimestampDao.get("Stops")
            val stopsGroupsLastFetchTimestamp = fetchTimestampDao.get("StopsGroups")

            var stopsValidFrom: Long? = null
            if (stopsLastFetchTimestamp != null) {
                stopsValidFrom = stopsLastFetchTimestamp.timestamp.time
            }

            var stopsGroupsValidFrom: Long? = null
            if (stopsGroupsLastFetchTimestamp != null) {
                stopsGroupsValidFrom = stopsGroupsLastFetchTimestamp.timestamp.time
            }

            startFetchStopsService(stopsValidFrom)
            startFetchStopsGroupsService(stopsGroupsValidFrom)

            val newStopsFetchTimestamp = Timestamp(System.currentTimeMillis())
            val newStopsGroupsFetchTimestamp = Timestamp(System.currentTimeMillis())

            fetchTimestampDao.insert(FetchTimestamp("Stops", newStopsFetchTimestamp))
            fetchTimestampDao.insert(FetchTimestamp("StopsGroups", newStopsGroupsFetchTimestamp))
        }
    }

    fun addStopToFavorites(id: String) {
        Log.d(TAG, "Adding stop $id to favorites")
        executors.diskIO.execute { favoriteStopDao.addFavoriteStop(FavoriteStop(id)) }
    }

    fun removeStopFromFavorites(id: String) {
        Log.d(TAG, "Removing stop $id from favorites")
        executors.diskIO.execute { favoriteStopDao.removeFavoriteStop(id) }
    }

    private fun startFetchStopsService(validFrom: Long?) {
        stopNetworkDataSource.startFetchStopsService(validFrom)
    }

    fun startFetchArrivals(id: String) {
        stopNetworkDataSource.startFetchArrivals(id)
    }

    fun fetchSelectedStopLines(id: String) {
        stopNetworkDataSource.startFetchStopLinesService(id)
    }

    private fun startFetchStopsGroupsService(validFrom: Long?) {
        stopsGroupNetworkDataSource.startFetchStopsGroupsService(validFrom)
    }

    fun getAllStopsGroups(): LiveData<List<StopsGroup>> {
        return stopsGroupDao.getStopsGroups()
    }

}