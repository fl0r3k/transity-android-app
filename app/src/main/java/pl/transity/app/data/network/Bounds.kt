package pl.transity.app.data.network

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Bounds(
        val north: Double,
        val east: Double,
        val south: Double,
        val west: Double
) {
    companion object {
        fun fromLatLngBounds(latLonBounds: LatLngBounds): Bounds {
            return Bounds(
                    latLonBounds.northeast.latitude,
                    latLonBounds.northeast.longitude,
                    latLonBounds.southwest.latitude,
                    latLonBounds.southwest.longitude)
        }
    }

    fun toLatLngBounds(): LatLngBounds {
        return LatLngBounds(LatLng(south, west), LatLng(north, east))
    }

    override fun toString(): String {
        return "(N: $north, S: $south, W: $west, E: $east)"
    }
}