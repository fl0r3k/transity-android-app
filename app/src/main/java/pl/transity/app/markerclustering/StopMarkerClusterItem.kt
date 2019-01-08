package pl.transity.app.markerclustering

import com.google.android.gms.maps.model.LatLng

const val TRAM = 0
const val BUS = 3

class StopMarkerClusterItem(
        private val id: Int,
        private val set: Int,
        private val type: Int,
        private val name: String,
        private val position: LatLng
): StopSetClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return "$id $type $name $position"
    }

    override fun getSet(): Int {
        return set
    }
}