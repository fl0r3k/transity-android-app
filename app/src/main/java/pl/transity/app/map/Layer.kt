package pl.transity.app.map

import android.util.Log
import com.google.android.gms.maps.GoogleMap


abstract class Layer : GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    protected var layerOnMap: Boolean = false
    protected var layerVisible: Boolean = false

    protected abstract fun redrawObjects()
    protected abstract fun removeObjects()

    fun isLayerOnMap() : Boolean {
        return layerOnMap
    }

    fun isLayerVisible() : Boolean {
        return layerVisible
    }

    open fun addLayerToMap() {
        Log.d(TAG,"addLayerToMap() - BEGIN")
        if (!layerOnMap) {
            Log.d(TAG,"addLayerToMap() - layer not on map, adding layer")
            layerOnMap = true
            layerVisible = false
        } else {
            Log.d(TAG,"addLayerToMap() - layer already on map, not adding layer")
        }
        Log.d(TAG,"addLayerToMap() - END")
    }

    open fun removeLayerFromMap() {
        Log.d(TAG,"removeLayerFromMap() - BEGIN")
        if (layerOnMap) {
            Log.d(TAG,"removeLayerFromMap() - layer on map, removing layer")
            layerOnMap = false
            layerVisible = false
            removeObjects()
        } else {
            Log.d(TAG,"removeLayerFromMap() - layer not on map, not removing layer")
        }
        Log.d(TAG,"removeLayerFromMap() - END")
    }

    @Synchronized
    fun showLayer(){
        Log.d(TAG,"showLayer() - BEGIN")
        if (layerOnMap){
            Log.d(TAG,"showLayer() - layer on map")
            if (!layerVisible){
                Log.d(TAG,"showLayer() - layer not visible, showing layer")
                layerVisible = true
                redrawObjects()
            }
        }
    }

    @Synchronized
    fun hideLayer(){
        Log.d(TAG,"hideLayer() - BEGIN")
        if (layerOnMap){
            Log.d(TAG,"hideLayer() - layer on map")
            if (layerVisible){
                Log.d(TAG,"hideLayer() - layer visible, hiding layer")
                layerVisible = false
                removeObjects()
            } else {
                Log.d(TAG,"hideLayer() - layer not visible, not hiding layer")
            }
        }
    }


    fun refresh() {
        if (layerOnMap && layerVisible) {
            redrawObjects()
        }
    }


    companion object {
        private const val TAG = "Layer"
    }
}
