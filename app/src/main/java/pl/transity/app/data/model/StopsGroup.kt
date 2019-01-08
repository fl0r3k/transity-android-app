package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose

@Entity(tableName = "stops_groups")
data class StopsGroup(
        @PrimaryKey
        @Expose
        val id: String,
        @Expose
        var name: String,
        @Expose
        val lat: Double,
        @Expose
        val lon: Double
) {
    @Ignore
    val position: LatLng = LatLng(lat, lon)

    override fun toString(): String {
        return "($id,$name,$lat,$lon,$position)"
    }
}