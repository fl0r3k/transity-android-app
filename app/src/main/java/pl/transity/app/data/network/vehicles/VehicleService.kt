package pl.transity.app.data.network.vehicles


import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp


class VehicleService(
        val vehicleApi: VehicleAPI
) {

    companion object {
        private val TAG = VehicleService::class.simpleName
        private var BASE_URL = "http://ztm-scsexpert.eu-central-1.elasticbeanstalk.com/api/v1/"
        private var INSTANCE: VehicleService? = null

        fun getInstance(): VehicleService =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildVehicleService().also { INSTANCE = it }
                }

        private fun buildVehicleService(): VehicleService {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(Timestamp::class.java, JsonDeserializer<Timestamp> { json, _, _ ->
                Timestamp(json.asJsonPrimitive.asLong) })
            val gson = gsonBuilder.create()

            return VehicleService(Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(VehicleAPI::class.java))
        }
    }
}