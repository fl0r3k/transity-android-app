package pl.transity.app.utilities

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import pl.transity.app.R
import pl.transity.app.data.model.Stop
import pl.transity.app.data.model.StopsGroup
import pl.transity.app.data.model.Vehicle
import pl.transity.app.data.network.Bounds

const val TRAM = 0
const val BUS = 3

const val VEHICLE_PATH_Z_INDEX = 4f
const val SECONDARY_ROUTE_FILL_Z_INDEX = 5f
const val SECONDARY_ROUTE_BORDER_Z_INDEX = 6f
const val PRIMARY_ROUTE_BORDER_Z_INDEX = 7f
const val PRIMARY_ROUTE_FILL_Z_INDEX = 8f
const val STOP_MARKER_Z_INDEX = 9f
const val VEHICLE_MARKER_Z_INDEX = 10f

const val VEHICLE_PATH_WIDTH = 15f
const val ROUTE_FILL_WIDTH = 17f
const val ROUTE_BORDER_WIDTH = 22f

const val ZOOM_CUTOFF_STOPS_VISIBLE = 15f
const val ZOOM_CUTOFF_VEHICLES_VISIBLE = 14f
const val ZOOM_CUTOFF_STOPS_GROUPS_VISIBLE = 12f

const val TAG = "MapPainter"

class MapPainter(private val context: Context, private val map: GoogleMap) {

    private val allStops = HashMap<String, Stop>()

    private val stopsMarkers = HashMap<String, Marker>()
    private val stopsGroupsMarkers = HashMap<String, Marker>()
    private var vehiclesMarkers = HashMap<String, Marker>()

    private var isVehicleSelected: Boolean = false
    private var selectedVehicleMarker: Marker? = null

    private var isSelectedVehicleRouteOnMap: Boolean = false
    private var routePolylines = HashMap<String, Polyline>()
    private var routeMarkers = HashMap<String, Marker>()


    fun addStopsMarkersToMap(stops: List<Stop>?) {
        if (stops != null) {

            val show = map.cameraPosition.zoom > ZOOM_CUTOFF_STOPS_VISIBLE

            val busStopMarkerOptions = stopMarkerOptions(BUS).visible(show)
            val tramStopMarkerOptions = stopMarkerOptions(TRAM).visible(show)
            val defaultMarkerOptions = stopMarkerOptions(null).visible(show)

            Log.d(TAG, "Adding stops to map (${stops.size})")
            for (s in stops) {
                if (stopsMarkers.containsKey(s.id)) continue
                val m = when (s.type) {
                    Stop.BUS -> map.addMarker(busStopMarkerOptions.position(s.position))
                    Stop.TRAM -> map.addMarker(tramStopMarkerOptions.position(s.position))
                    else -> map.addMarker(defaultMarkerOptions.position(s.position))
                }
                m.tag = s
                m.title = s.id
                stopsMarkers[s.id] = m
            }

            Log.d(TAG, "Removing out of bounds stops from map")
            val b = Bounds.fromLatLngBounds(map.projection.visibleRegion.latLngBounds)
            val iterator = stopsMarkers.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val stop = entry.value.tag as Stop
                val lat = stop.lat
                val lon = stop.lon
                if (lat < b.south || lat > b.north || lon < b.west || lon > b.east) {
                    entry.value.remove()
                    iterator.remove()
                }
            }
        } else {
            Log.d(TAG, "Removing all stops from map")
            for ((_, m) in stopsMarkers)
                m.remove()
            stopsMarkers.clear()
        }
    }

    fun addStopsGroupsMarkersToMap(stopsGroups: List<StopsGroup>?) {
        if (stopsGroups != null) {

            val show = map.cameraPosition.zoom > ZOOM_CUTOFF_STOPS_GROUPS_VISIBLE
            val stopsGroupMarkerOptions = stopMarkerOptions(null).visible(show)

            Log.d(TAG, "Adding stops groups to map (${stopsGroups.size})")
            for (sg in stopsGroups) {
                if (stopsGroupsMarkers.containsKey(sg.id)) continue
                val m = map.addMarker(stopsGroupMarkerOptions.position(sg.position))
                m.tag = sg
                stopsGroupsMarkers[sg.id] = m
            }

            Log.d(TAG, "Removing out of bounds stops groups from map")
            val b = Bounds.fromLatLngBounds(map.projection.visibleRegion.latLngBounds)
            val iterator = stopsGroupsMarkers.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val stopsGroup = entry.value.tag as StopsGroup
                val lat = stopsGroup.lat
                val lon = stopsGroup.lon
                if (lat < b.south || lat > b.north || lon < b.west || lon > b.east) {
                    entry.value.remove()
                    iterator.remove()
                }
            }

        } else {
            Log.d(TAG, "Removing all stops groups from map")
            for ((_, m) in stopsGroupsMarkers)
                m.remove()
            stopsGroupsMarkers.clear()
        }
    }

    fun addVehiclesMarkersToMap(vehicles: List<Vehicle>?) {
        if (vehicles != null && !isVehicleSelected) {
            Log.d(TAG, "Adding vehicles to map (${vehicles.size})")

            for (v in vehicles) {
                if (vehiclesMarkers.containsKey(v.id)) {
                    Log.d(TAG, "Updating ${v.id}")
                    val m = vehiclesMarkers[v.id]!!
                    if (m.rotation > 180 && v.bearing < 180) {
                        val icon = drawVehicleMarkerIcon(v.line, "RIGHT")
                        m.setIcon(icon)
                    } else if (m.rotation < 180 && v.bearing > 180) {
                        val icon = drawVehicleMarkerIcon(v.line, "LEFT")
                        m.setIcon(icon)
                    }
                    m.rotation = v.bearing.toFloat()
                    m.position = LatLng(v.lat, v.lon)
                    m.tag = v
                } else {
                    Log.d(TAG, "Adding $v")
                    val side = if (v.bearing < 180) "RIGHT" else "LEFT"
                    val vehicleMarkerOptions = vehicleMarkerOptions(v.line, side)
                            .position(LatLng(v.lat, v.lon))
                            .rotation(v.bearing.toFloat())
                    val m = map.addMarker(vehicleMarkerOptions)
                    m.tag = v
                    vehiclesMarkers[v.id] = m
                }
            }

            Log.d(TAG, "Removing out of bounds vehicles from map")
            val b = Bounds.fromLatLngBounds(map.projection.visibleRegion.latLngBounds)
            val iter = vehiclesMarkers.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                val vehicle = entry.value.tag as Vehicle
                val lat = vehicle.lat
                val lon = vehicle.lon
                if (lat < b.south || lat > b.north || lon < b.west || lon > b.east) {
                    entry.value.remove()
                    iter.remove()
                }
            }
        } else {
            Log.d(TAG, "Removing all vehicles from map")
            for ((_, m) in vehiclesMarkers)
                m.remove()
            vehiclesMarkers.clear()
        }
    }

    fun addSelectedVehicleToMap(v: Vehicle) {
        Log.d(TAG, "Adding/Updating selected vehicle to/on map: ${v.id}")
        isVehicleSelected = true
        if (selectedVehicleMarker == null) {
            val side = if (v.bearing < 180) "RIGHT" else "LEFT"
            val vehicleMarkerOptions = vehicleMarkerOptions(v.line, side)
                    .position(LatLng(v.lat, v.lon))
                    .rotation(v.bearing.toFloat())
            val m = map.addMarker(vehicleMarkerOptions)
            m.tag = v
            selectedVehicleMarker = m

        } else {
            Log.d(TAG, "Updating ${v.id}")
            val m = selectedVehicleMarker!!
            if (m.rotation > 180 && v.bearing < 180) {
                val icon = drawVehicleMarkerIcon(v.line, "RIGHT")
                m.setIcon(icon)
            } else if (m.rotation < 180 && v.bearing > 180) {
                val icon = drawVehicleMarkerIcon(v.line, "LEFT")
                m.setIcon(icon)
            }
            m.rotation = v.bearing.toFloat()
            m.position = LatLng(v.lat, v.lon)
            m.tag = v
            selectedVehicleMarker = m


        }
        Log.d(TAG, "Finished adding/updating selected vehicle to/on map")
    }

    fun removeSelectedVehicleFromMap() {
        if (selectedVehicleMarker != null) {
            selectedVehicleMarker?.remove()
            selectedVehicleMarker = null
            isVehicleSelected = false
        }
    }

    fun addSelectedVehicleRouteToMap(v: Vehicle) {
        if (!isSelectedVehicleRouteOnMap) {
            Log.d(TAG, "Adding route ${v.route} to map")

            isSelectedVehicleRouteOnMap = true

            routePolylines["BORDER"] = map.addPolyline(route(context!!, true, false).addAll(v.routeAsLatLngList()))
            routePolylines["FILL"] = map.addPolyline(route(context!!, true, true).addAll(v.routeAsLatLngList()))

            if (v.stops != null) {
                val normal = routeStopMarker(false)
                val onDemand = routeStopMarker(true)
                for (sq in v.stops) {
                    val s = allStops[sq.id]
                    if (s != null) {
                        val m = when (sq.pick) {
                            3 -> map.addMarker(onDemand.position(s.position))
                            else -> map.addMarker(normal.position(s.position))
                        }
                        m.tag = s
                        routeMarkers[s.id] = m
                    }
                }
            }
        }
    }

    fun removeSelectedVehicleRouteFromMap() {
        for ((_, m) in routeMarkers) m.remove()
        routeMarkers.clear()
        for ((_, v) in routePolylines) v.remove()
        routePolylines.clear()
        isSelectedVehicleRouteOnMap = false
    }

    private fun stopMarkerOptions(type: Int?): MarkerOptions {
        val stopIcon = when (type) {
            BUS -> BitmapDescriptorFactory.fromResource(R.drawable.ic_stop_bus)
            TRAM -> BitmapDescriptorFactory.fromResource(R.drawable.ic_stop_tram)
            else -> BitmapDescriptorFactory.fromResource(R.drawable.ic_stop)
        }
        return MarkerOptions().icon(stopIcon).anchor(0.5f, 0.5f).zIndex(STOP_MARKER_Z_INDEX)
    }

    private fun vehicleMarkerOptions(line: String, side: String): MarkerOptions {
        return MarkerOptions().icon(drawVehicleMarkerIcon(line, side))
                .anchor(0.5f, 0.5f)
                .zIndex(VEHICLE_MARKER_Z_INDEX)
    }

    fun routeStopMarker(onDemand: Boolean): MarkerOptions {
        return MarkerOptions().icon(drawSelectedRouteStop(onDemand, false)).zIndex(STOP_MARKER_Z_INDEX)
    }

    private fun drawSelectedRouteStop(onDemand: Boolean, small: Boolean): BitmapDescriptor {
        return if (onDemand) {
            if (!small) BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_on_demand)
            else BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_small_on_demand)
        } else {
            if (!small) BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop)
            else BitmapDescriptorFactory.fromResource(R.drawable.ic_route_stop_small)
        }
    }

    private fun drawVehicleMarkerIcon(line: String, side: String): BitmapDescriptor {
        val opt = BitmapFactory.Options()
        opt.inMutable = true
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.vehicle, opt)
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
        textPaint.color = Color.BLACK
        textPaint.textSize = 36F
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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

    fun route(context: Context, primary: Boolean, fill: Boolean): PolylineOptions {
        val color: Int
        val width: Float
        val zIndex: Float
        if (primary) {
            if (fill) {
                color = ContextCompat.getColor(context, R.color.routePrimaryFill)
                width = ROUTE_FILL_WIDTH
                zIndex = PRIMARY_ROUTE_FILL_Z_INDEX
            } else {
                color = ContextCompat.getColor(context, R.color.routePrimaryBorder)
                width = ROUTE_BORDER_WIDTH
                zIndex = PRIMARY_ROUTE_BORDER_Z_INDEX
            }
        } else {
            if (fill) {
                color = ContextCompat.getColor(context, R.color.routeSecondaryFill)
                width = ROUTE_FILL_WIDTH
                zIndex = SECONDARY_ROUTE_FILL_Z_INDEX
            } else {
                color = ContextCompat.getColor(context, R.color.routeSecondaryBorder)
                width = ROUTE_BORDER_WIDTH
                zIndex = SECONDARY_ROUTE_BORDER_Z_INDEX
            }
        }

        return PolylineOptions()
                .startCap(RoundCap()).endCap(RoundCap())
                .color(color)
                .width(width)
                .zIndex(zIndex)
    }

    fun path(context: Context, speed: Double): PolylineOptions {
        val color = if (speed < 10) {
            ContextCompat.getColor(context, R.color.speedDarkRed)
        } else if (speed >= 10 && speed < 20) {
            ContextCompat.getColor(context, R.color.speedRed)
        } else if (speed >= 20 && speed < 30) {
            ContextCompat.getColor(context, R.color.speedOrange)
        } else if (speed > 30) {
            ContextCompat.getColor(context, R.color.speedGreen)
        } else {
            Color.parseColor("#000000")
        }

        return PolylineOptions()
                .startCap(RoundCap()).endCap(RoundCap())
                .color(color)
                .width(VEHICLE_PATH_WIDTH)
                .zIndex(VEHICLE_PATH_Z_INDEX)
    }
}