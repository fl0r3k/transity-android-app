package pl.transity.app.map

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pl.transity.app.R
import pl.transity.app.data.model.Stop
import pl.transity.app.map.utils.MarkerAnimator
import pl.transity.app.viewmodels.MainActivityViewModel
import java.util.*

private const val Z_INDEX = 9f
private const val TAG = "StopsLayer"

class StopsLayer(private val map: GoogleMap,
                 private val viewModel: MainActivityViewModel,
                 private val lifecycleOwner: LifecycleOwner) : Layer() {

    private var busStopMarkerOptions: MarkerOptions = MarkerOptions().anchor(0.5f, 0.5f).zIndex(Z_INDEX)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop)).alpha(0f)
    private var tramStopMarkerOptions: MarkerOptions = MarkerOptions().anchor(0.5f, 0.5f).zIndex(Z_INDEX)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tram_stop)).alpha(0f)
    private var defaultStopMarkerOptions: MarkerOptions = MarkerOptions().anchor(0.5f, 0.5f).zIndex(Z_INDEX)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rail_station)).alpha(0f)

    private val markers = HashMap<String, Marker>()
    var stops = emptyList<Stop>()


    override fun redrawObjects() {
        Log.d(TAG, "redrawObjects() - BEGIN")
        val bounds = map.projection.visibleRegion.latLngBounds

        val iterator = markers.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val stop = entry.value.tag as Stop
            if (!bounds.contains(stop.position)) {
                entry.value.remove()
                iterator.remove()
            }
        }

        for (stop in stops) {
            if (bounds.contains(stop.position)) {
                if (markers.containsKey(stop.id)) continue
                val marker = when (stop.type) {
                    Stop.BUS -> map.addMarker(busStopMarkerOptions.position(stop.position))
                    Stop.TRAM -> map.addMarker(tramStopMarkerOptions.position(stop.position))
                    else -> map.addMarker(defaultStopMarkerOptions.position(stop.position))
                }
                MarkerAnimator.fadeIn(marker)
                marker.tag = stop
                markers[stop.id] = marker
            }
        }
        Log.d(TAG, "redrawObjects() - END")
    }

    override fun removeObjects() {
        Log.d(TAG, "removeObjects() - BEGIN")
        for ((_, marker) in markers) {
            marker.remove()
        }
        markers.clear()
        Log.d(TAG, "removeObjects() - END")
    }

    override fun onCameraIdle() {
        if (layerOnMap && layerVisible) {
            redrawObjects()
        }
    }

    override fun onMarkerClick(m: Marker): Boolean {
        if (m.tag is Stop) {
            val s = m.tag as Stop
            viewModel.setSelectedStop(s)
            return true
        }
        return false
    }
}