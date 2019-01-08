package pl.transity.app.data.model

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import java.sql.Timestamp


class StopArrival(
        @Expose val id: String,
        @Expose val type: Int,
        @Expose val name: String,
        @Expose val desc: String,
        @Expose val lat: Double,
        @Expose val lon: Double,
        @Expose val zone: Int,
        @Expose val arrival: Timestamp,
        @Expose val departure: Timestamp,
        @Expose val pick: Int,
        @Expose val drop: Int,
        @Expose val seq: Int,
        var distance: Double
) : SortedListAdapter.ViewModel {

    val position: LatLng
        get() = LatLng(lat, lon)

    override fun <T : Any?> isSameModelAs(item: T): Boolean {
        if (item is StopArrival) {
            val stopArrival = item as StopArrival
            return id == stopArrival.id
        }
        return false
    }

    override fun <T : Any?> isContentTheSameAs(item: T): Boolean {
        if (item is StopArrival) {
            val stopArrival = item as StopArrival
            return arrival == stopArrival.arrival
        }
        return false
    }
}