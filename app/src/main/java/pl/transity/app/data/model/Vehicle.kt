package pl.transity.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

class Vehicle(
        @Expose val type: Int,
        @Expose val line: String,
        @Expose val brigade: String,
        @Expose val route: String,
        @Expose val service: String,
        @Expose val trip: String,
        @Expose val source: String,
        @Expose val destination: String,
        @Expose val start: Timestamp,
        @Expose val end: Timestamp,
        @Expose val lat: Double,
        @Expose val lon: Double,
        @Expose val bearing: Double,
        @Expose val speed: Double,
        @Expose val odometer: Double,
        @Expose val time: Timestamp,
        @SerializedName("time_diff") val timeDiff: Timestamp,
        @Expose val timestamp: Timestamp,
        @Expose val progress: Int,
        @Expose val count: Int,
        @SerializedName("next_stop") val nextStop: StopArrival,
        @Expose val stops: List<StopArrival>,
        @SerializedName("route_geometry") val routeGeometry: List<RoutePoint>,
        @Expose val steps: List<PathPoint>
) {
    val id: String
        get() = "$line/$brigade"

    val position: LatLng
        get() = LatLng(lat, lon)

    fun routeAsLatLngList(): List<LatLng> {
        val latLngList = mutableListOf<LatLng>()
        if (routeGeometry != null) for (g in routeGeometry) latLngList.add(g.toLatLon())
        return latLngList
    }

    override fun toString(): String {
        return "($id,$type,$line,$brigade,$lat,$lon,$bearing,$odometer,$speed,$time,$timestamp)"
    }

    companion object {
        const val TRAM = 0
        const val BUS = 3
    }
}