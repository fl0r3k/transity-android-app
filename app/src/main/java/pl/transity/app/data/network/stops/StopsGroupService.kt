package pl.transity.app.data.network.stops

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StopsGroupService(
        val stopsGroupApi : StopsGroupAPI
) {

    companion object {
        private val TAG = StopsGroupService::class.simpleName
        private var BASE_URL = "http://ztm-scsexpert.eu-central-1.elasticbeanstalk.com/api/v1/"
        private var INSTANCE: StopsGroupService? = null

        fun getInstance(): StopsGroupService =
                INSTANCE ?: synchronized(this) {
                    INSTANCE
                            ?: buildStopService().also { INSTANCE = it }
                }

        private fun buildStopService() : StopsGroupService =
                StopsGroupService(Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(StopsGroupAPI::class.java))
    }
}