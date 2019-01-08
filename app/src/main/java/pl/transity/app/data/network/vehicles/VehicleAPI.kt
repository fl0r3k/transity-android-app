package pl.transity.app.data.network.vehicles

import pl.transity.app.data.model.Route
import pl.transity.app.data.model.Vehicle
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleAPI {

    @GET("vehicles/lines/{line}/brigades/{brigade}")
    fun getVehicle(
            @Path("line") line : String,
            @Path("brigade") brigade : String,
            @Query("timestamp") timestamp: Long?
    ) : Call<Vehicle>

    @GET("vehicles")
    fun getVehiclesInBounds(
            @Query("north") north : Double,
            @Query("south") south : Double,
            @Query("west") west : Double,
            @Query("east") east : Double
            ) : Call<List<Vehicle>>

    @GET("vehicles")
    fun getSpecificLinesInBounds(
            @Query("north") north : Double,
            @Query("south") south : Double,
            @Query("west") west : Double,
            @Query("east") east : Double,
            @Query("lines") lines : String
    ) : Call<List<Vehicle>>

    @GET("lines/{line}/routes/")
    fun getLineRoutes(@Path("line") line : String) : Call<List<Route>>

    @GET("lines/{line}/routes/{route}")
    fun getRoute(@Path("line") line : String,@Path("route") route : String) : Call<Route>
}