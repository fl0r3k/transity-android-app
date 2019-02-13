package pl.transity.app.data.network.vehicles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.transity.app.AppExecutors
import pl.transity.app.data.model.Vehicle
import pl.transity.app.data.network.Bounds
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

private const val TAG = "VehicleNetworkDS"

class VehicleNetworkDataSource(
        private val vehicleApi: VehicleAPI,
        private val executors: AppExecutors
) {

    companion object {
        private var INSTANCE: VehicleNetworkDataSource? = null

        fun getInstance(stopApi: VehicleAPI, executors: AppExecutors): VehicleNetworkDataSource =
                INSTANCE
                        ?: synchronized(this) {
                            INSTANCE
                                    ?: VehicleNetworkDataSource(stopApi, executors).also { INSTANCE = it }
                        }
    }


    var fetchBounds: Bounds? = null
    var onlyFavorite = false
    private var favouriteLines = ""

    private val vehiclesFetcher = Runnable { fetch() }

    private var isFetcherRunning = false

    private var fetcherHandler: ScheduledFuture<*>? = null

    private val fetchedVehicles = MutableLiveData<List<Vehicle>>()
    private val fetchedSelectedVehicle = MutableLiveData<Vehicle>()

    fun getFetchedVehicles(): LiveData<List<Vehicle>> {
        return fetchedVehicles
    }

    fun getFetchedSelectedVehicle(): LiveData<Vehicle> {
        return fetchedSelectedVehicle
    }

    fun setFavoriteLines(lines : List<String>){
        favouriteLines = lines.joinToString(",")
    }

    fun startFetcher() {
        if (!isFetcherRunning) {
            fetcherHandler = executors.sheduler
                    .scheduleWithFixedDelay(vehiclesFetcher, 0, 1000, TimeUnit.MILLISECONDS)
            isFetcherRunning = true
        }
    }

    fun stopFetcher() {
        if (isFetcherRunning) {
            fetcherHandler!!.cancel(true)
            isFetcherRunning = false
        }
    }

    private fun fetch() {
        Log.d(TAG, "Preparing request on region: $fetchBounds")
        if (fetchBounds != null) {
            val north = fetchBounds!!.north
            val south = fetchBounds!!.south
            val west = fetchBounds!!.west
            val east = fetchBounds!!.east

            val call = if (onlyFavorite) {
                vehicleApi.getSpecificLinesInBounds(north, south, west, east, favouriteLines)
            } else {
                vehicleApi.getVehiclesInBounds(north, south, west, east)
            }

            Log.d(TAG, "Enqueueing request")
            call.enqueue(object : Callback<List<Vehicle>> {
                override fun onResponse(call: Call<List<Vehicle>>, response: retrofit2.Response<List<Vehicle>>) {
                    Log.d(TAG, "Response received")
                    Log.d(TAG, response.body().toString())
                    val vehicles = response.body()
                    if (vehicles != null) {
                        Log.d(TAG, "Fetched vehicles size is ${vehicles.size}")
                        fetchedVehicles.postValue(vehicles)
                    }
                }

                override fun onFailure(call: Call<List<Vehicle>>, t: Throwable) {
                    if (t.message != null) Log.e(TAG, t.message)
                    t.printStackTrace()
                }
            })
        }
    }


    fun fetchSelectedVehicleDetails(id: String) {
        Log.d(TAG, "Splitting id")
        val splittedId = id.split("/")
        Log.d(TAG, "Preparing call")
        val call = vehicleApi.getVehicle(splittedId[0], splittedId[1], null)
        Log.d(TAG, "Enqueueing call")
        call.enqueue(object : Callback<Vehicle> {
            override fun onResponse(call: Call<Vehicle>, response: retrofit2.Response<Vehicle>) {
                Log.d(TAG, "Parsing response")
                Log.d(TAG, response.body().toString())
                val vehicle = response.body()
                if (vehicle != null) {
                    Log.d(TAG, "Fetched vehicle $vehicle")
                    fetchedSelectedVehicle.postValue(vehicle)
//                    if (fetchedSelectedVehicle.value == null) {
//                        fetchedSelectedVehicle.postValue(vehicle)
//                    } else {
//                        val currentVehicle = fetchedSelectedVehicle.value
//                        if (vehicle.time > currentVehicle!!.time) {
//                            vehicle.setId()
//                            fetchedSelectedVehicle.postValue(vehicle)
//                        }
//                    }
                }
            }

            override fun onFailure(call: Call<Vehicle>, t: Throwable) {
                if (t.message != null) Log.e(TAG, t.message)
                t.printStackTrace()
            }
        })
    }
}