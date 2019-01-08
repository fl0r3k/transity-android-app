package pl.transity.app.data.network.stops

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp

class StopService(
        val stopApi: StopAPI
) {

    companion object {
        private val TAG = StopService::class.simpleName
        private var BASE_URL = "http://ztm-scsexpert.eu-central-1.elasticbeanstalk.com/api/v1/"
        private var INSTANCE: StopService? = null

        fun getInstance(): StopService =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildStopService().also { INSTANCE = it }
                }

        private fun buildStopService(): StopService {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(Timestamp::class.java, JsonDeserializer<Timestamp> { json, _, _ ->
                Timestamp(json.asJsonPrimitive.asLong)
            })
            val gson = gsonBuilder.create()

            return StopService(Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(StopAPI::class.java))
        }

    }
}