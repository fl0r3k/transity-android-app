package pl.transity.app.data.network.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import pl.transity.app.AppExecutors
import pl.transity.app.data.model.Arrival
import pl.transity.app.data.model.Stop
import pl.transity.app.data.model.StopLine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StopNetworkDataSource(
        private val stopApi: StopAPI,
        private val executors: AppExecutors
) {

    companion object {
        private val TAG = StopNetworkDataSource::class.simpleName
        private var INSTANCE: StopNetworkDataSource? = null

        fun getInstance(stopApi: StopAPI, executors: AppExecutors): StopNetworkDataSource =
                INSTANCE
                        ?: synchronized(this) {
                            Log.d(TAG, "Creating or Returning Instance")
                            INSTANCE
                                    ?: StopNetworkDataSource(stopApi, executors).also { INSTANCE = it }
                        }
    }

    private val downloadedStops = MutableLiveData<List<Stop>>().default(emptyList())
    private val downloadedStopLines = MutableLiveData<List<StopLine>>().default(emptyList())
    private val downloadedArrivals = MutableLiveData<List<Arrival>>().default(emptyList())

    fun getFetchedStops(): LiveData<List<Stop>> {
        return downloadedStops
    }

    fun getFetchedStopLines(): LiveData<List<StopLine>> {
        return downloadedStopLines
    }

    fun getFetchedArrivals(): LiveData<List<Arrival>> {
        return downloadedArrivals
    }


    fun startFetchStopsService(validFromLong: Long?) {
        executors.networkIO.execute { fetchStops(validFromLong) }
    }

    fun startFetchStopLinesService(id: String) {
        executors.networkIO.execute { fetchStopLines(id) }
    }

    fun startFetchArrivals(id: String) {
        executors.networkIO.execute { fetchArrivals(id) }
    }

    private fun fetchStops(validFromLong: Long?) {
        Log.d(TAG, "Fetch stops started")
        val call = stopApi.getAllStops(validFromLong)
        call.enqueue(object : Callback<List<Stop>> {
            override fun onResponse(call: Call<List<Stop>>, response: Response<List<Stop>>) {
                Log.d(TAG, "Response ${response.code()}")
                if (response.isSuccessful) {
                    val stops = response.body()
                    if (stops != null) {
                        Log.d(TAG, "Fetched stops size is ${stops!!.size}")
                        if (stops.isNotEmpty()) downloadedStops.value = stops
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<Stop>>, t: Throwable) {
                Log.e(TAG, t.message)
                t.printStackTrace()
            }
        })
    }

    private fun fetchStopLines(id: String) {
        Log.d(TAG, "Fetch stop lines started $id")
        val call = stopApi.getStopLines(id)
        call.enqueue(object : Callback<List<StopLine>> {
            override fun onResponse(call: Call<List<StopLine>>, response: Response<List<StopLine>>) {
                Log.d(TAG, "Response ${response.code()}")
                if (response.isSuccessful) {
                    val stopLines = response.body()
                    if (stopLines != null) {
                        Log.d(TAG, "Fetched stop lines size is ${stopLines!!.size}")
                        if (stopLines.isNotEmpty()) downloadedStopLines.value = stopLines
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<StopLine>>, t: Throwable) {
                Log.e(TAG, t.message)
                t.printStackTrace()
            }
        })
    }

    private fun fetchArrivals(id: String) {
        Log.d(TAG, "Fetch arrivals for stop id $id")
        val call = stopApi.getNextArrivals(id)
        call.enqueue(object : Callback<List<Arrival>> {
            override fun onResponse(call: Call<List<Arrival>>, response: Response<List<Arrival>>) {
                Log.d(TAG, "Response ${response.code()}")
                if (response.isSuccessful) {
                    val arrivals = response.body()
                    if (arrivals != null) {
                        Log.d(TAG, "Fetched arrivals size is ${arrivals.size}")
                        downloadedArrivals.value = arrivals
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<Arrival>>, t: Throwable) {
                if (t.message != null) Log.e(TAG, t.message)
                t.printStackTrace()
            }
        })
    }
}

private fun <T> MutableLiveData<T>.default(initialValue: T): MutableLiveData<T> = apply { value = initialValue }