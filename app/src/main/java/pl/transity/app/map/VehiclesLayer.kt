package pl.transity.app.map

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import pl.transity.app.R
import pl.transity.app.constants.RouteType
import pl.transity.app.data.model.Vehicle
import pl.transity.app.data.network.Bounds
import pl.transity.app.map.utils.LatLngInterpolator
import pl.transity.app.map.utils.MarkerAnimator
import pl.transity.app.utilities.*
import pl.transity.app.viewmodels.MainActivityViewModel
import java.util.HashMap
import androidx.core.content.res.ResourcesCompat


private const val Z_INDEX = 10f
private const val TAG = "VehiclesLayer"

class VehiclesLayer(
        private val map: GoogleMap,
        private val context: Context,
        private val viewModel: MainActivityViewModel,
        private val lifecycleOwner: LifecycleOwner
) : Layer() {


    private val fontSize = context.resources.getDimensionPixelSize(R.dimen.vehicle_line_font_size).toFloat()
    private var vehicleMarkerOptions: MarkerOptions = MarkerOptions().anchor(0.5f, 0.5f).zIndex(Z_INDEX)

    var vehicles = ArrayMap<String, Vehicle>()
    private val markers = HashMap<String, Marker>()

    private var isVehicleSelected = false
    private var selectedVehicle: String? = null

    private var isSelectedVehicleRouteOnMap = false
    private var routePolylines = HashMap<String, Polyline>()
    private var routeStops = HashMap<String, Marker>()

    override fun addLayerToMap() {
        Log.d(TAG, "addLayerToMap() - BEGIN")
        if (!layerOnMap) {
            Log.d(TAG, "addLayerToMap() - layer not on map, adding layer")
            layerOnMap = true
            layerVisible = false
            startObservers()
        } else {
            Log.d(TAG, "addLayerToMap() - layer already on map, not adding layer")
        }
        Log.d(TAG, "addLayerToMap() - END")
    }

    override fun removeLayerFromMap() {
        Log.d(TAG, "removeLayerFromMap() - BEGIN")
        if (layerOnMap) {
            Log.d(TAG, "removeLayerFromMap() - layer on map, removing layer")
            layerOnMap = false
            layerVisible = false
            stopObservers()
            removeObjects()
        } else {
            Log.d(TAG, "removeLayerFromMap() - layer not on map, not removing layer")
        }
        Log.d(TAG, "removeLayerFromMap() - END")
    }

    private fun startObservers() {
        viewModel.vehicles.observe(lifecycleOwner, Observer { vehicles ->
            if (vehicles != null) {
                val vehiclesArrayMap = ArrayMap<String, Vehicle>(vehicles.size)
                for (vehicle in vehicles) vehiclesArrayMap[vehicle.id] = vehicle
                this.vehicles = vehiclesArrayMap
                refresh()
            }
        })
        viewModel.getVehiclesLayerVisibility().observe(lifecycleOwner, Observer { isVisible ->
            Log.d(TAG, "vehiclesLayerVisible.observe $isVisible")
            if (isVisible) {
                viewModel.startVehicleFetcher()
                showLayer()
            } else {
                viewModel.stopVehicleFetcher()
                hideLayer()
            }
        })
        viewModel.isVehicleSelected().observe(lifecycleOwner, Observer { isSelected ->
            Log.d(TAG, "isVehicleSelected().observe $isSelected")
            if (!isSelected) {
                isVehicleSelected = false
                selectedVehicle = null
                removeSelectedVehicleRouteFromMap()
            }
        })
        viewModel.getSelectedVehicle().observe(lifecycleOwner, Observer { selectedVehicle ->
            Log.d(TAG, "getSelectedVehicle().observe $selectedVehicle")
            this.selectedVehicle = selectedVehicle
        })
        viewModel.selectedVehicleData.observe(lifecycleOwner, Observer { vehicleData ->
            Log.d(TAG, "selectedVehicleData.observe $vehicleData")
            if (vehicleData != null) {
                if (isSelectedVehicleRouteOnMap) removeSelectedVehicleRouteFromMap()
                drawSelectedVehicleRoute(vehicleData)
            }
        })

    }

    private fun stopObservers() {
        viewModel.vehicles.removeObservers(lifecycleOwner)
        viewModel.getVehiclesLayerVisibility().removeObservers(lifecycleOwner)
        viewModel.isVehicleSelected().removeObservers(lifecycleOwner)
        viewModel.getSelectedVehicle().removeObservers(lifecycleOwner)
        viewModel.selectedVehicleData.removeObservers(lifecycleOwner)
    }


    override fun redrawObjects() {
        val iterator = markers.entries.iterator()
        while (iterator.hasNext()) {
            val marker = iterator.next().value
            val vehicle = marker.tag as Vehicle
            if (!vehicles.contains(vehicle.id)) {
                MarkerAnimator.fadeOutAndRemove(marker)
                iterator.remove()
            }
        }

        for ((id, vehicle) in vehicles) {
            val position = LatLng(vehicle.lat, vehicle.lon)
            val rotation = vehicle.bearing.toFloat()
            if (markers.containsKey(id)) {
                val marker = markers[id]!!
                if (marker.position != position) {
                    Log.d(TAG, "Updating vehicle $id")
                    if (marker.rotation > 180 && vehicle.bearing < 180) {
                        marker.setIcon(prepareVehicleMarkerIcon(vehicle.line, vehicle.type, "RIGHT"))
                    } else if (marker.rotation < 180 && vehicle.bearing > 180) {
                        marker.setIcon(prepareVehicleMarkerIcon(vehicle.line, vehicle.type, "LEFT"))
                    }
                    MarkerAnimator.animateMarkerPosition(marker, position, LatLngInterpolator.Linear())
                    MarkerAnimator.animateMarkerRotation(marker, rotation)
                    marker.tag = vehicle
                }
            } else {
                Log.d(TAG, "Adding vehicle $id")
                val side = if (vehicle.bearing < 180) "RIGHT" else "LEFT"
                val vehicleMarkerOptions = vehicleMarkerOptions
                        .position(position)
                        .rotation(vehicle.bearing.toFloat())
                        .icon(prepareVehicleMarkerIcon(vehicle.line, vehicle.type, side))
                val marker = map.addMarker(vehicleMarkerOptions)
                MarkerAnimator.fadeIn(marker)
                marker.tag = vehicle
                markers[id] = marker
            }
        }
    }

    override fun removeObjects() {
        val iterator = markers.entries.iterator()
        while (iterator.hasNext()) {
            val marker = iterator.next().value
            MarkerAnimator.fadeOutAndRemove(marker)
            iterator.remove()
        }
    }

    private fun drawSelectedVehicleRoute(v: Vehicle) {
        if (!isSelectedVehicleRouteOnMap) {
            Log.d(TAG, "Adding route ${v.route} to map")

            isSelectedVehicleRouteOnMap = true

            routePolylines["BORDER"] = map.addPolyline(route(context, v.type, false).addAll(v.routeAsLatLngList()))
            routePolylines["FILL"] = map.addPolyline(route(context, v.type, true).addAll(v.routeAsLatLngList()))

            if (v.stops != null) {
                val normal = routeStopMarker(v.type,false)
                val onDemand = routeStopMarker(v.type,true)
                for (st in v.stops) {
                    if (st != null) {
                        val m = when (st.pick) {
                            3 -> map.addMarker(onDemand.position(st.position))
                            else -> map.addMarker(normal.position(st.position))
                        }
                        m.tag = st
                        routeStops[st.id] = m
                    }
                }
            }
        }
    }

    private fun removeSelectedVehicleRouteFromMap() {
        if (isSelectedVehicleRouteOnMap) {
            for ((_, m) in routeStops) m.remove()
            routeStops.clear()

            for ((_, v) in routePolylines) v.remove()
            routePolylines.clear()

            isSelectedVehicleRouteOnMap = false
        }
    }

    override fun onCameraIdle() {
        viewModel.setVehicleFetchRegion(Bounds.fromLatLngBounds(map.projection.visibleRegion.latLngBounds))
    }

    override fun onMarkerClick(m: Marker): Boolean {
        if (m.tag is Vehicle) {
            val v = m.tag as Vehicle
//            if (isVehicleSelected) {
//                if (v.id != selectedVehicle!!.id) {
//                    viewModel.setSelectedVehicle(v)
//                    removeSelectedVehicleRouteFromMap()
//                }
//            } else {
            viewModel.setSelectedVehicle(v.id)
//            }
            return true
        }
        return false
    }

    private fun prepareVehicleMarkerIcon(line: String, type: Int, side: String): BitmapDescriptor {
        val opt = BitmapFactory.Options()
        opt.inMutable = true
        val bitmap = if (type == RouteType.BUS) BitmapFactory.decodeResource(context.resources, R.drawable.marker_bus_purple, opt)
        else if (type == RouteType.TRAM) BitmapFactory.decodeResource(context.resources, R.drawable.marker_tram_red, opt)
        else BitmapFactory.decodeResource(context.resources, R.drawable.vehicle, opt)

//        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.vehicle, opt)
        val canvas = Canvas(bitmap)

        val r = canvas.clipBounds
        val h = r.height()
        val w = r.width()
        val textCenterX = w / 2f
        val textCenterY = 2f / 3f * h - 5f

//        println("canvas height: $h")
//        println("canvas width: $w")
//        println("rotation x: $textCenterX")
//        println("rotation y: $textCenterY")

        canvas.save()
        canvas.translate(textCenterX, textCenterY)
        if (side == "LEFT") canvas.rotate(90f)
        else canvas.rotate(-90f)

//        val r1 = canvas.clipBounds
//        val t = r1.top
//        val b = r1.bottom
//        val l = r1.left
//        val ri = r1.right
//        println("bmp top: $t")
//        println("bmp bottom: $b")
//        println("bmp left: $l")
//        println("bmp right: $ri")
//        val circlePaint = Paint()
//        circlePaint.color = Color.RED
//        circlePaint.style = Paint.Style.FILL
//        canvas.drawCircle(0f,0f,25f,circlePaint)

        val textPaint = Paint()
//        textPaint.color = Color.BLACK
        textPaint.color = Color.WHITE
//        textPaint.textSize = 36F
        textPaint.textSize = fontSize
        textPaint.textAlign = Paint.Align.LEFT
//        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.lato_bold)

        println("text style:" + textPaint.typeface.style)
        textPaint.getTextBounds(line, 0, line.length, r)

//        println("text height: " + r.height())
//        println("text width: " + r.width())
//        println("text left: " + r.left)
//        println("text right: " + r.right)
//        println("text top: " + r.top)
//        println("text bottom: " + r.bottom)


        val x = -r.width() / 2f - r.left
        val y = r.height() / 2f + r.bottom

//        println("text x: $x")
//        println("text y: $y")

        canvas.drawText(line, x, y, textPaint)
        canvas.restore()

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun route(context: Context, type: Int, fill: Boolean): PolylineOptions {
        val color: Int
        val width: Float
        val zIndex: Float
        if (type == RouteType.BUS) {
            if (fill) {
                color = ContextCompat.getColor(context, R.color.busPrimaryLight)
//                color = ContextCompat.getColor(context, R.color.busRoutePrimaryFill)
                width = ROUTE_FILL_WIDTH
                zIndex = PRIMARY_ROUTE_FILL_Z_INDEX
            } else {
                color = ContextCompat.getColor(context, R.color.busPrimaryDark)
//                color = ContextCompat.getColor(context, R.color.busRoutePrimaryBorder)
                width = ROUTE_BORDER_WIDTH
                zIndex = PRIMARY_ROUTE_BORDER_Z_INDEX
            }
        } else if (type == RouteType.TRAM) {
            if (fill) {
                color = ContextCompat.getColor(context, R.color.tramPrimaryLight)
                width = ROUTE_FILL_WIDTH
                zIndex = PRIMARY_ROUTE_FILL_Z_INDEX
            } else {
                color = ContextCompat.getColor(context, R.color.tramPrimaryDark)
                width = ROUTE_BORDER_WIDTH
                zIndex = PRIMARY_ROUTE_BORDER_Z_INDEX
            }
        } else {
            if (fill) {
                color = ContextCompat.getColor(context, R.color.routePrimaryFill)
                width = ROUTE_FILL_WIDTH
                zIndex = PRIMARY_ROUTE_FILL_Z_INDEX
            } else {
                color = ContextCompat.getColor(context, R.color.routePrimaryBorder)
                width = ROUTE_BORDER_WIDTH
                zIndex = PRIMARY_ROUTE_BORDER_Z_INDEX
            }
        }
        return PolylineOptions()
                .startCap(RoundCap()).endCap(RoundCap())
                .color(color)
                .width(width)
                .zIndex(zIndex)
    }

    private fun routeStopMarker(type: Int, onDemand: Boolean): MarkerOptions {
        return MarkerOptions()
                .icon(drawSelectedRouteStop(type, onDemand, false))
                .zIndex(STOP_MARKER_Z_INDEX)
                .anchor(0.5f, 0.5f)
    }

    private fun drawSelectedRouteStop(type: Int, onDemand: Boolean, small: Boolean): BitmapDescriptor {
        return when (type) {
            RouteType.BUS -> BitmapDescriptorFactory.fromResource(R.drawable.bus_stop)
            RouteType.TRAM -> BitmapDescriptorFactory.fromResource(R.drawable.tram_stop)
            else -> BitmapDescriptorFactory.fromResource(R.drawable.rail_station)
        }
//        return if (onDemand) {
//            if (!small) BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_on_demand)
//            else BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_small_on_demand)
//        } else {
//            if (!small) BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop)
//            else BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_small)
//        }
    }
}