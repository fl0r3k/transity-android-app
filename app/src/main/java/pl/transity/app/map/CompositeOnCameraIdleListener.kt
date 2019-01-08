package pl.transity.app.map

import com.google.android.gms.maps.GoogleMap

class CompositeOnCameraIdleListener : GoogleMap.OnCameraIdleListener {

    private val listeners = ArrayList<GoogleMap.OnCameraIdleListener>()

    fun add(listener: GoogleMap.OnCameraIdleListener) {
        listeners.add(listener)
    }

    fun remove(listener: GoogleMap.OnCameraIdleListener) {
        listeners.remove(listener)
    }

    override fun onCameraIdle() {
        listeners.forEach { it.onCameraIdle() }
    }

}