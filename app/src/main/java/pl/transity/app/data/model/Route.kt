package pl.transity.app.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose

data class Route(
        @Expose
        val id: String,
        @Expose
        val direction: Int,
        @Expose
        var primary: Boolean,
        @Expose
        val source: String,
        @Expose
        val destination: String,
        @Expose
        val steps: List<Point>?,
        @Expose
        val stops: List<StopSeq>?
) {
    override fun toString(): String {
        return "($id,$direction,$primary,$source,$destination,$direction,${steps?.size})"
    }

    fun stepsAsLatLng(): List<LatLng> {
        val latLngList = mutableListOf<LatLng>()
        if (steps != null) for (s in steps) latLngList.add(LatLng(s.lat, s.lon))
        return latLngList
    }
}