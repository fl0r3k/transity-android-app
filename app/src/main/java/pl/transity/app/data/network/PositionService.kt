package pl.transity.app.data.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp


class PositionService(
        val positionApi: PositionAPI
) {

    companion object {
        private val TAG = PositionService::class.simpleName
        private var BASE_URL = "http://ztm-scsexpert.eu-central-1.elasticbeanstalk.com/api/v1/"
        private var INSTANCE: PositionService? = null

        fun getInstance(): PositionService =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildPositionService().also { INSTANCE = it }
                }

        private fun buildPositionService(): PositionService {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(Timestamp::class.java, JsonDeserializer<Timestamp> { json, _, _ ->
                Timestamp(json.asJsonPrimitive.asLong) })
            val gson = gsonBuilder.create()

            return PositionService(Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(PositionAPI::class.java))
        }
    }
}