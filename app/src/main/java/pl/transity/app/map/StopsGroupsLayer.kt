package pl.transity.app.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pl.transity.app.R
import pl.transity.app.data.model.StopsGroup
import pl.transity.app.map.utils.MarkerAnimator
import java.util.HashMap

private const val Z_INDEX = 9f
private const val TAG = "StopsGroupsLayer"

class StopsGroupsLayer(private val map: GoogleMap) : Layer() {

    private var markerOptions: MarkerOptions = MarkerOptions().anchor(0.5f, 0.5f).zIndex(Z_INDEX)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_group_stop))

    var stopsGroups = emptyList<StopsGroup>()
    private val markers = HashMap<String, Marker>()

    override fun redrawObjects() {
        val bounds = map.projection.visibleRegion.latLngBounds

        val iterator = markers.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val stop = entry.value.tag as StopsGroup
            if (!bounds.contains(stop.position)) {
                entry.value.remove()
                iterator.remove()
            }
        }

        for (stopsGroup in stopsGroups) {
            if (bounds.contains(stopsGroup.position)) {
                if (markers.containsKey(stopsGroup.id)) continue
                val marker = map.addMarker(markerOptions.position(stopsGroup.position))
                MarkerAnimator.fadeIn(marker)
                marker.tag = stopsGroup
                markers[stopsGroup.id] = marker
            }
        }

    }

    override fun removeObjects() {
        for ((_, marker) in markers) {
            marker.remove()
        }
        markers.clear()
    }

    override fun onCameraIdle() {
        if (layerOnMap && layerVisible) {
            redrawObjects()
        }
    }

    override fun onMarkerClick(m: Marker): Boolean {
        return true
    }

}