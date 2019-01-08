package pl.transity.app.data.network.stops

import pl.transity.app.data.model.Arrival
import pl.transity.app.data.model.Stop
import pl.transity.app.data.model.StopLine
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StopAPI {

    @GET("stops")
    fun getAllStops(@Query("timestamp") timestamp : Long?) : Call<List<Stop>>

    @GET("stops/{id}/lines")
    fun getStopLines(@Path("id") id : String) : Call<List<StopLine>>

    @GET("stops/{id}/timetable/lines")
    fun getNextArrivals(@Path("id") id : String) : Call<List<Arrival>>
}