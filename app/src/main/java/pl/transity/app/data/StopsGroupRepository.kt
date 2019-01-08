package pl.transity.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import pl.transity.app.AppExecutors
import pl.transity.app.data.database.FetchTimestampDao
import pl.transity.app.data.database.StopsGroupDao
import pl.transity.app.data.model.FetchTimestamp
import pl.transity.app.data.model.StopsGroup
import pl.transity.app.data.network.Bounds
import pl.transity.app.data.network.stops.StopsGroupNetworkDataSource
import java.sql.Timestamp


class StopsGroupRepository(
        private val stopsGroupDao: StopsGroupDao,
        private val fetchTimestampDao: FetchTimestampDao,
        private val stopsGroupNetworkDataSource: StopsGroupNetworkDataSource,
        private val executors: AppExecutors) {


    companion object {
        private val TAG = StopsGroupRepository::class.simpleName
        private var INSTANCE: StopsGroupRepository? = null

        fun getInstance(stopsGroupDao: StopsGroupDao, fetchTimestampDao: FetchTimestampDao, stopsGroupNetworkDataSource: StopsGroupNetworkDataSource, executors: AppExecutors): StopsGroupRepository =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: StopsGroupRepository(stopsGroupDao, fetchTimestampDao, stopsGroupNetworkDataSource, executors).also { INSTANCE = it }
                }
    }

    private val networkData = stopsGroupNetworkDataSource.getFetchedStopsGroups()
    private val bounds = MutableLiveData<Bounds>()
    private val stopsGroupsInBoundsCache = MutableLiveData<List<StopsGroup>>()

    init {
        Log.d(TAG, "Initializing")

        bounds.observeForever { b ->
            if (b != null)
                executors.diskIO.execute{
                    stopsGroupsInBoundsCache.postValue(stopsGroupDao.getStopsGroupsInBounds(b.north, b.south, b.west, b.east))
                }
            else
                stopsGroupsInBoundsCache.value = null
        }

        networkData.observeForever { newStopsGroupsFromNetwork ->
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
            val lastFetchTimestamp = fetchTimestampDao.get("StopsGroups")
            var validFrom: Long? = null
            if (lastFetchTimestamp != null) {
                validFrom = lastFetchTimestamp.timestamp.time
            }
            startFetchStopsGroupsService(validFrom)
            val newFetchTimestamp = Timestamp(System.currentTimeMillis())
            fetchTimestampDao.insert(FetchTimestamp("StopsGroups", newFetchTimestamp))
        }
    }

    private fun startFetchStopsGroupsService(validFrom: Long?) {
        stopsGroupNetworkDataSource.startFetchStopsGroupsService(validFrom)
    }

    fun setBounds(newBounds: Bounds?) {
        bounds.value = newBounds
    }

    fun getStopsGroups(): LiveData<List<StopsGroup>> {
        return stopsGroupsInBoundsCache
    }
}