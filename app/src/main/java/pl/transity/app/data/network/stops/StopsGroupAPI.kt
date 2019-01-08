package pl.transity.app.data.network.stops

import pl.transity.app.data.model.StopsGroup
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StopsGroupAPI {

    @GET("stops-sets")
    fun getAllStopsGroups(@Query("timestamp") timestamp: Long?): Call<List<StopsGroup>>

}