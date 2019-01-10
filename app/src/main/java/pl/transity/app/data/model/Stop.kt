package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose

@Entity(tableName = "stops")
data class Stop(
        @PrimaryKey @Expose val id: String,
        @Expose val type: Int,
        @Expose var name: String,
        @Expose val lat: Double,
        @Expose val lon: Double
) : SortedListAdapter.ViewModel{
    @Ignore
    val position: LatLng = LatLng(lat,lon)

    companion object {
        const val TRAM = 0
        const val BUS = 3
    }

    override fun toString(): String {
        return "($id,$type,$name,$lat,$lon,$position)"
    }

    override fun <T : Any?> isSameModelAs(item: T): Boolean {
        if (item is Stop) {
            val stop = item as Stop
            return stop.id == id
        }
        return false
    }

    override fun <T : Any?> isContentTheSameAs(item: T): Boolean {
        if (item is Stop) {
            val stop = item as Stop
            return stop.id == id
        }
        return false
    }
}