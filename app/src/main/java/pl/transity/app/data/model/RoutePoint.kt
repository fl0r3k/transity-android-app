package pl.transity.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose


class RoutePoint(
        @Expose val lat: Double,
        @Expose val lon: Double,
        @Expose val seq: Int
) {
    override fun toString(): String {
        return "($lat,$lon,$seq)"
    }
    fun toLatLon() : LatLng {
        return LatLng(lat,lon)
    }
}
