package pl.transity.app.utilities

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

class CityLocation(
        val latitude: Double,
        val longitude: Double,
        val zoom: Float
) {
    fun toCameraUpdate(): CameraUpdate {
        return CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom)
    }
}