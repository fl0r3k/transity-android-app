package pl.transity.app.data.network

import pl.transity.app.data.model.Route
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface LineAPI {

    @GET("lines/{line}/routes")
    fun getRoutes(@Path("line") line : String) : Call<List<Route>>

}