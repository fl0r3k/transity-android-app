package pl.transity.app.data.model

import com.google.gson.annotations.Expose


data class Point(
        @Expose val lat: Double,
        @Expose val lon: Double
) {
    override fun toString(): String {
        return "(lat: $lat, lon: $lon)"
    }
}
