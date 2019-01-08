package pl.transity.app.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import pl.transity.app.data.model.VehiclePath

interface PositionAPI {

    @GET("positions/{line}/{brigade}")
    fun getVehicle(@Path("line") line : String, @Path("brigade") brigade : String) : Call<VehiclePath>

}