package pl.transity.app.data.network.stops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.util.Log
import pl.transity.app.AppExecutors
import pl.transity.app.data.model.StopsGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StopsGroupNetworkDataSource(
        private val context: Context,
        private val stopsGroupApi: StopsGroupAPI,
        private val executors: AppExecutors
) {

    companion object {
        private val TAG = StopsGroupNetworkDataSource::class.simpleName
        private var INSTANCE: StopsGroupNetworkDataSource? = null

        fun getInstance(context: Context, stopsGroupApi: StopsGroupAPI, executors: AppExecutors): StopsGroupNetworkDataSource =
                INSTANCE ?: synchronized(this) {
                    Log.d(TAG, "Creating or Returning Instance")
                    INSTANCE
                            ?: StopsGroupNetworkDataSource(context, stopsGroupApi, executors).also { INSTANCE = it }
                }
    }

    private val downloadedStopsGroups = MutableLiveData<List<StopsGroup>>()

    fun getFetchedStopsGroups(): LiveData<List<StopsGroup>> {
        return downloadedStopsGroups
    }

    fun startFetchStopsGroupsService(validFromLong: Long?) {
        executors.networkIO.execute { fetchStopsGroups(validFromLong) }
    }

    private fun fetchStopsGroups(validFromLong: Long?) {
        Log.d(TAG, "Fetch stops groups started")
        val call = stopsGroupApi.getAllStopsGroups(validFromLong)
        call.enqueue(object : Callback<List<StopsGroup>> {
            override fun onResponse(call: Call<List<StopsGroup>>, response: Response<List<StopsGroup>>) {
                Log.d(TAG, "Response ${response.code()}")
                if (response.isSuccessful) {
                    val stopsGroups = response.body()
                    if (stopsGroups != null) {
                        Log.d(TAG, "Fetched stops groups size is ${stopsGroups!!.size}")
                        if (stopsGroups.isNotEmpty()) downloadedStopsGroups.value = stopsGroups
                    }
                } else {
                    Log.d(TAG, "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<StopsGroup>>, t: Throwable) {
                if (t.message != null) Log.e(TAG, t.message)
                t.printStackTrace()
            }
        })
    }
}