package pl.transity.app.data.model

import com.google.gson.annotations.Expose
import java.sql.Timestamp

class PathPoint(
        @Expose val lat: Double,
        @Expose val lon: Double,
        @Expose val speed: Double,
        @Expose val time: Timestamp
) {
    override fun toString(): String {
        return "($lat,$lon,$speed,$time)"
    }
}
