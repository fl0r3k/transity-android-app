package pl.transity.app.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CompositeOnMarkerClickListener : GoogleMap.OnMarkerClickListener {

    private val listeners = ArrayList<GoogleMap.OnMarkerClickListener>()

    fun add(listener: GoogleMap.OnMarkerClickListener) {
        listeners.add(listener)
    }

    fun remove(listener: GoogleMap.OnMarkerClickListener) {
        listeners.remove(listener)
    }

    override fun onMarkerClick(m: Marker): Boolean {
        listeners.forEach { it.onMarkerClick(m) }
        return true
    }

}