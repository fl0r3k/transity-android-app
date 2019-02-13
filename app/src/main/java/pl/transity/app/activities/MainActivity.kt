package pl.transity.app.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import pl.transity.app.R
import pl.transity.app.adapter.LinesIconsAdapter
import pl.transity.app.adapter.NextArrivalListItemClickListener
import pl.transity.app.adapter.NextArrivalsAdapter
import pl.transity.app.adapter.NextStopsAdapter
import pl.transity.app.adapter.decorator.StopLinesItemDecorator
import pl.transity.app.data.model.*
import pl.transity.app.databinding.ActivityMainBinding
import pl.transity.app.map.*
import pl.transity.app.ui.OnLocationPermissionGrantedListener
import pl.transity.app.utilities.*
import pl.transity.app.viewmodels.MainActivityViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(),
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        NextArrivalListItemClickListener {

    private lateinit var dataBinding: ActivityMainBinding


    private val alphabeticalComparator = Comparator<StopLine> { a, b -> a.line.compareTo(b.line) }
    private val arrivalTimeComparator = Comparator<Arrival> { a, b -> a.arrival.compareTo(b.arrival) }
    private val stopArrivalTimeComparator = Comparator<StopArrival> { a, b -> a.arrival.compareTo(b.arrival) }

    private lateinit var stopsLayer: StopsLayer
    private lateinit var stopsLayerVisibilityObserver: Observer<Boolean>
    private lateinit var stopsObserver: Observer<List<Stop>>
    private lateinit var selectedStopLinesObserver: Observer<List<StopLine>>
    private lateinit var selectedStopArrivalsObserver: Observer<List<Arrival>>

    private lateinit var favoriteStopsLayer: StopsLayer
    private lateinit var favoriteStopsLayerVisibilityObserver: Observer<Boolean>
    private lateinit var favoriteStopsObserver: Observer<List<Stop>>

    private lateinit var stopsGroupsLayer: StopsGroupsLayer
    private lateinit var stopsGroupsLayerVisibilityObserver: Observer<Boolean>
    private lateinit var stopsGroupsObserver: Observer<List<StopsGroup>>

    private lateinit var vehiclesLayer: VehiclesLayer

    private lateinit var selectedVehicleStopsArrivalsObserver: Observer<List<StopArrival>>

    private lateinit var vehiclesLayerVisibilityObserver: Observer<Boolean>
    private lateinit var vehiclesObserver: Observer<List<Vehicle>>

    private lateinit var favoriteVehiclesLayer: VehiclesLayer
    private lateinit var favoriteVehiclesLayerVisibilityObserver: Observer<Boolean>
    private lateinit var favoriteVehiclesObserver: Observer<List<Vehicle>>

    private val compositeOnCameraIdleListener = CompositeOnCameraIdleListener()
    private val compositeOnMarkerClickListener = CompositeOnMarkerClickListener()


    private lateinit var currentCity : CityLocation

    private val DEFAULT_LOCATION = LatLng(52.2297700, 21.0117800)
    private val DEFAULT_ZOOM = 16f
    private val DEFAULT_CAMERA_POSITION = CameraPosition.Builder()
            .target(DEFAULT_LOCATION)
            .zoom(DEFAULT_ZOOM)
            .build()


//    private lateinit var adView: AdView

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var onLocationPermissionGrantedListenerCallback: OnLocationPermissionGrantedListener
    private lateinit var onMyLocationButtonClickListenerCallback: GoogleMap.OnMyLocationButtonClickListener

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var googleMap: GoogleMap


    private lateinit var propertyChangedCallback: OnPropertyChangedCallback

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() - START")

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        dataBinding.mainactivity = this

        setupMainActivityViewModel()
        dataBinding.viewModel = viewModel

        currentCity = CityLocation(52.2297700,21.0117800,11f)

        setupSharedPreferences()

        (map as SupportMapFragment).getMapAsync(this)

//        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        adView.loadAd(adRequest)

        setupToolbar()
        setupBottomSheet()

        val activity = this as Activity
        propertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                activity.invalidateOptionsMenu()
            }
        }

        Log.d(TAG, "onCreate() - END")
    }

    override fun onStart() {
        Log.d(TAG, "onStart() - BEGIN")
        super.onStart()
        dataBinding.viewModel?.addOnPropertyChangedCallback(propertyChangedCallback)
        Log.d(TAG, "onStart() - END")
    }

    override fun onResume() {
        Log.d(TAG, "onResume() - BEGIN")
        super.onResume()
        if (::vehiclesLayer.isInitialized && ::viewModel.isInitialized) {
            if (vehiclesLayer.isLayerOnMap() && vehiclesLayer.isLayerVisible()) viewModel.startVehicleFetcher()
        }
        Log.d(TAG, "onResume() - END")
    }

    //Size correcting utility functions
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun clipToStatusBar(view: View) {
        val statusBarHeight = getStatusBarHeight()
        view.layoutParams.height += statusBarHeight
        view.setPadding(0, statusBarHeight, 0, 0)
    }


    private fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }


    private fun setupMainActivityViewModel() {
        Log.d(TAG, "setupMainActivityViewModel() - START")

        val factoryViewModelFactory = Injection.provideMainActivityViewModelFactory(this)
        viewModel = ViewModelProviders
                .of(this, factoryViewModelFactory)
                .get(MainActivityViewModel::class.java)

        Log.d(TAG, "setupMainActivityViewModel() - END")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.favorite).isVisible = !dataBinding.viewModel!!.isFavoriteSelected
        menu.findItem(R.id.unfavorite).isVisible = dataBinding.viewModel!!.isFavoriteSelected

        return true
    }

    override fun onPause() {
        Log.d(TAG, "onPause() - BEGIN")
        super.onPause()
        if (::vehiclesLayer.isInitialized && ::viewModel.isInitialized) {
            if (vehiclesLayer.isLayerOnMap()) viewModel.stopVehicleFetcher()
        }
        if (::googleMap.isInitialized) saveMapState()
        Log.d(TAG, "onPause() - END")
    }

    override fun onStop() {
        Log.d(TAG, "onStop() - BEGIN")
        super.onStop()
        dataBinding.viewModel?.removeOnPropertyChangedCallback(propertyChangedCallback)
        Log.d(TAG, "onStop() - END")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() - BEGIN")
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        viewModel.disposeSubscriptions()
        Log.d(TAG, "onDestroy() - END")
    }

    private fun saveMapState() {
        val ed = PreferenceManager.getDefaultSharedPreferences(this).edit()
        ed.apply {
            putFloat("MAP_ZOOM", googleMap.cameraPosition.zoom)
            putLong("MAP_LATITUDE", googleMap.cameraPosition.target.latitude.toBits())
            putLong("MAP_LONGITUDE", googleMap.cameraPosition.target.longitude.toBits())
        }
        ed.apply()
    }

    private fun restoreMapState() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val zoom = sp.getFloat("MAP_ZOOM", currentCity.zoom)
        val lat = Double.fromBits(sp.getLong("MAP_LATITUDE", currentCity.latitude.toBits()))
        val lon = Double.fromBits(sp.getLong("MAP_LONGITUDE", currentCity.longitude.toBits()))

        val location = CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), zoom)
        googleMap.moveCamera(location)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady() -  START")

        this.googleMap = googleMap

        this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_standard))

        restoreMapState()

        // Check for location permission
        if (Permissions.isLocationPermissionGranted(this)) {
            setupLocation()
        } else {
            Permissions.askLocationPermission(this)
        }

        addLayers()

        compositeOnCameraIdleListener.add(this)
        compositeOnMarkerClickListener.add(this)

        this.googleMap.setOnCameraIdleListener(compositeOnCameraIdleListener)
        this.googleMap.setOnMarkerClickListener(compositeOnMarkerClickListener)

        Log.d(TAG, "onMapReady() - END")
    }

    private fun addLayers() {
        stopsLayer = StopsLayer(googleMap, viewModel, this)
        stopsGroupsLayer = StopsGroupsLayer(googleMap)
        favoriteStopsLayer = StopsLayer(googleMap, viewModel, this)
        vehiclesLayer = VehiclesLayer(googleMap, this, viewModel, this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val showStops = sharedPreferences.getBoolean(getString(R.string.pref_show_stops_key), resources.getBoolean(R.bool.pref_show_stops_default))
        if (showStops) {
            val onlyFavoriteStops = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_stops_key), resources.getBoolean(R.bool.pref_show_only_favorite_stops_default))
            if (onlyFavoriteStops)
                addFavoriteStopsLayer()
            else
                addStopsLayer()
        }
        val showVehicles = sharedPreferences.getBoolean(getString(R.string.pref_show_vehicles_key), resources.getBoolean(R.bool.pref_show_vehicles_default))
        if (showVehicles) {
            val onlyFavoriteVehicles = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_vehicles_key), resources.getBoolean(R.bool.pref_show_only_favorite_vehicles_default))
            Log.d(TAG, "$onlyFavoriteVehicles")
            viewModel.setOnlyFavoriteVehicles(onlyFavoriteVehicles)
            addVehiclesLayer()
        }
    }

    private fun addStopsLayer() {
        stopsLayer.addLayerToMap()
        stopsGroupsLayer.addLayerToMap()

        compositeOnCameraIdleListener.add(stopsLayer)
        compositeOnCameraIdleListener.add(stopsGroupsLayer)
        compositeOnMarkerClickListener.add(stopsLayer)
        compositeOnMarkerClickListener.add(stopsGroupsLayer)


        stopsObserver = Observer { stops ->
            stopsLayer.stops = stops
            stopsLayer.refresh()
        }
        viewModel.stops.observe(this, stopsObserver)

        stopsLayerVisibilityObserver = Observer { isVisible ->
            Log.d(TAG, "stopsLayerVisible.observe $isVisible")
            if (isVisible) stopsLayer.showLayer() else stopsLayer.hideLayer()
        }
        viewModel.getStopsLayerVisibility().observe(this, stopsLayerVisibilityObserver)

        selectedStopLinesObserver = Observer { lines ->
            (bottomSheetLinesList.adapter as LinesIconsAdapter).edit()
                    .replaceAll(lines).commit()
        }
        viewModel.selectedStopLines.observe(this, selectedStopLinesObserver)

        selectedStopArrivalsObserver = Observer { arrivals ->
            //            val newAdapter = NextArrivalsAdapter(arrivals, this)
//            bottomSheetNextArrivalsList.swapAdapter(newAdapter, true)
            Log.d(TAG, "selectedStopArrivalsObserver.observe $arrivals")
            (bottomSheetNextArrivalsList.adapter as NextArrivalsAdapter).edit().removeAll()
                    .add(arrivals).commit()
        }
        viewModel.arrivals.observe(this, selectedStopArrivalsObserver)

        stopsGroupsObserver = Observer { groups ->
            stopsGroupsLayer.stopsGroups = groups
        }
        viewModel.stopsGroups.observe(this, stopsGroupsObserver)

        stopsGroupsLayerVisibilityObserver = Observer { isVisible ->
            Log.d(TAG, "stopsGroupsLayerVisibilityObserver.observe $isVisible")
            if (isVisible) stopsGroupsLayer.showLayer() else stopsGroupsLayer.hideLayer()
        }
        viewModel.getStopsGroupsLayerVisibility().observe(this, stopsGroupsLayerVisibilityObserver)
    }

    private fun removeStopsLayer() {
        viewModel.stops.removeObservers(this)
        viewModel.getStopsVisible().removeObservers(this)
        viewModel.selectedStopLines.removeObservers(this)
        viewModel.stopsGroups.removeObservers(this)
        viewModel.getStopsGroupsLayerVisibility().removeObservers(this)

        compositeOnCameraIdleListener.remove(stopsLayer)
        compositeOnCameraIdleListener.remove(stopsGroupsLayer)
        compositeOnMarkerClickListener.remove(stopsLayer)
        compositeOnMarkerClickListener.remove(stopsGroupsLayer)

        stopsLayer.removeLayerFromMap()
        stopsGroupsLayer.removeLayerFromMap()
    }

    private fun addFavoriteStopsLayer() {
        favoriteStopsLayer.addLayerToMap()
        compositeOnCameraIdleListener.add(favoriteStopsLayer)
        compositeOnMarkerClickListener.add(favoriteStopsLayer)

        favoriteStopsObserver = Observer { favoriteStops ->
            favoriteStopsLayer.stops = favoriteStops
            favoriteStopsLayer.refresh()
        }
        viewModel.favoriteStops.observe(this, favoriteStopsObserver)

        favoriteStopsLayerVisibilityObserver = Observer { isVisible ->
            Log.d(TAG, "favoriteStopsLayerVisible.observe $isVisible")
            if (isVisible) favoriteStopsLayer.showLayer() else favoriteStopsLayer.hideLayer()
        }
        viewModel.getFavoriteStopsLayerVisibility().observe(this, favoriteStopsLayerVisibilityObserver)

        selectedStopLinesObserver = Observer { lines ->
            //            val newAdapter = LinesIconsAdapter(lines)
//            bottomSheetLinesList.swapAdapter(newAdapter, true)
            (bottomSheetLinesList.adapter as LinesIconsAdapter).edit()
                    .replaceAll(lines).commit()
        }
        viewModel.selectedStopLines.observe(this, selectedStopLinesObserver)
    }

    private fun removeFavoriteStopsLayer() {
        viewModel.favoriteStops.removeObservers(this)
        viewModel.getFavoriteStopsLayerVisibility().removeObservers(this)
        viewModel.selectedStopLines.removeObservers(this)

        compositeOnCameraIdleListener.remove(favoriteStopsLayer)
        compositeOnMarkerClickListener.remove(favoriteStopsLayer)

        favoriteStopsLayer.removeLayerFromMap()
    }

    private fun addVehiclesLayer() {
        vehiclesLayer.addLayerToMap()
        compositeOnCameraIdleListener.add(vehiclesLayer)
        compositeOnMarkerClickListener.add(vehiclesLayer)

        selectedVehicleStopsArrivalsObserver = Observer { stopsArrivals ->
            //            val newAdapter = NextStopsAdapter(stopsArrivals)
//            bottomSheetNextStopsList.swapAdapter(newAdapter, true)
            (bottomSheetNextStopsList.adapter as NextStopsAdapter).edit().removeAll()
                    .add(stopsArrivals).commit()
        }
        viewModel.stopsArrivals.observe(this, selectedVehicleStopsArrivalsObserver)

//        viewModel.startVehicleFetcher()
    }

    private fun removeVehiclesLayer() {
        viewModel.stopVehicleFetcher()

        compositeOnCameraIdleListener.remove(vehiclesLayer)
        compositeOnMarkerClickListener.remove(vehiclesLayer)

        vehiclesLayer.removeLayerFromMap()
    }

    override fun onCameraIdle() {
        Log.d(TAG, "onCameraIdle() -  START")
        viewModel.setZoom(googleMap.cameraPosition.zoom)
        Log.d(TAG, "onCameraIdle() -  END")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.d(TAG, "onMarkerClick() -  START")

        Log.d(TAG, "onMarkerClick() -  ${googleMap.projection.toScreenLocation(marker.position)}")

        val offsetY = DisplayUtils.getHeightInPixels(this) / 4 - toolbar.layoutParams.height / 2
        val markerScreenLocation = googleMap.projection.toScreenLocation(marker.position)
        markerScreenLocation.y = markerScreenLocation.y + offsetY

        val position = googleMap.projection.fromScreenLocation(markerScreenLocation)

        Log.d(TAG, "onMarkerClick() -  $markerScreenLocation")

        val newCameraPosition = CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                        .target(position)
                        .zoom(googleMap.cameraPosition.zoom)
                        .build())
        googleMap.animateCamera(newCameraPosition)

        Log.d(TAG, "onMarkerClick() -  END")
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "ActionBar item clicked")
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "Clicked R.id.home")
                PaddingUtils.setPaddingTop(map.view!!, 0)
                viewModel.clearSelection()
                true
            }
            R.id.favorite -> {
                Log.d(TAG, "Clicked R.id.favorite")
                viewModel.addObjectToFavorites()
                true
            }
            R.id.unfavorite -> {
                Log.d(TAG, "Clicked R.id.unfavorite")
                viewModel.removeObjectFromFavorites()
                true
            }
            else -> {
                Log.d(TAG, "Clicked something else ${item.itemId}")
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        if (appBar.visibility == View.VISIBLE) {
            viewModel.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        viewModel.clearSelection()
    }

    @SuppressLint("MissingPermission")
    private fun setupLocation() {
        Log.d(TAG, "setupLocation() -  START")
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        viewModel.isLocationEnabled.set(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        goToMyLocation()
        Log.d(TAG, "setupLocation() -  END")
    }

    @SuppressLint("MissingPermission")
    private fun goToMyLocation() {
        fusedLocationClient.lastLocation
                .addOnSuccessListener(this) { location ->
                    if (location != null) {
                        val cameraPosition = CameraPosition.Builder()
                                .target(LatLng(location.latitude, location.longitude))
                                .zoom(16f)
                                .build()
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }
                }
    }

    fun myLocationButtonPressed() {
        if (Permissions.isLocationPermissionGranted(this)) {
            goToMyLocation()
        } else {
            Permissions.askLocationPermission(this)
        }
    }


    fun centerMapOnCity() {
        googleMap.animateCamera(currentCity.toCameraUpdate())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult() -  START")
        val permissionsMap = HashMap<String, Int>()
        for ((index, permission) in permissions.withIndex()) {
            permissionsMap[permission] = grantResults[index]
            Log.d(TAG, permission)
        }
        if (Permissions.isLocationPermissionGranted(this)) {
            Log.d(TAG, "Location permission granted")
            setupLocation()
        }
        Log.d(TAG, "onRequestPermissionsResult() -  END")
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val tag = "onSharedPreferenceChanged()"
        Log.d(TAG, "$tag - BEGIN")
        when (key) {
            getString(R.string.pref_show_vehicles_key) -> {
                val show = sharedPreferences.getBoolean(getString(R.string.pref_show_vehicles_key), resources.getBoolean(R.bool.pref_show_vehicles_default))
                if (show) {
                    Log.d(TAG, "$tag - adding vehicles layer")
                    addVehiclesLayer()
                } else {
                    Log.d(TAG, "$tag - removing vehicles layer")
                    removeVehiclesLayer()
                }
            }
            getString(R.string.pref_show_only_favorite_vehicles_key) -> {
                val onlyFavorites = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_vehicles_key), resources.getBoolean(R.bool.pref_show_only_favorite_vehicles_default))
                Log.d(TAG, "$onlyFavorites")
                viewModel.setOnlyFavoriteVehicles(onlyFavorites)
            }
            getString(R.string.pref_show_stops_key) -> {
                val show = sharedPreferences.getBoolean(getString(R.string.pref_show_stops_key), resources.getBoolean(R.bool.pref_show_stops_default))
                if (show) {
                    Log.d(TAG, "$tag - adding stops layer")
                    val onlyFavorites = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_stops_key), resources.getBoolean(R.bool.pref_show_only_favorite_stops_default))
                    if (onlyFavorites)
                        addFavoriteStopsLayer()
                    else
                        addStopsLayer()
                } else {
                    Log.d(TAG, "$tag - removing stops layer")
                    val onlyFavorites = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_stops_key), resources.getBoolean(R.bool.pref_show_only_favorite_stops_default))
                    if (onlyFavorites)
                        removeFavoriteStopsLayer()
                    else
                        removeStopsLayer()
                }
            }
            getString(R.string.pref_show_only_favorite_stops_key) -> {
                val show = sharedPreferences.getBoolean(getString(R.string.pref_show_only_favorite_stops_key), resources.getBoolean(R.bool.pref_show_only_favorite_stops_default))
                if (show) {
                    Log.d(TAG, "$tag - adding stops layer")
                    removeStopsLayer()
                    addFavoriteStopsLayer()
                } else {
                    Log.d(TAG, "$tag - removing stops layer")
                    removeFavoriteStopsLayer()
                    addStopsLayer()
                }
            }
        }
        Log.d(TAG, "$tag - END")
    }

    private fun fillToToolBar(view: View) {
        val toolbarHeight = toolbar.layoutParams.height
        val adViewHeight = AdSize.SMART_BANNER.getHeightInPixels(this)
        val bottomSheetDecoratorHeight = bottomSheetDecorator.layoutParams.height
        view.layoutParams.height = DisplayUtils.getHeightInPixels(this) - toolbarHeight - adViewHeight + bottomSheetDecoratorHeight
    }

    private fun setupToolbar() {
        clipToStatusBar(toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupBottomSheet() {

        fillToToolBar(bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = DisplayUtils.getHeightInPixels(this) / 2

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                if (slideOffset <= 0) {
//                    val paddingBottom = Math.round(displayHeight + (slideOffset - 1) / 2 * displayHeight)
//                    PaddingUtils.setPaddingBottom(map.view!!, paddingBottom)
//                }
            }
        })

        val linesListLayoutManager = LinearLayoutManager(this)
        linesListLayoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
        bottomSheetLinesList.apply {
            layoutManager = linesListLayoutManager
            addItemDecoration(StopLinesItemDecorator(context as Activity, 16, 2, 32))
            adapter = LinesIconsAdapter(context, alphabeticalComparator)
            setHasFixedSize(false)
        }

        bottomSheetNextArrivalsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NextArrivalsAdapter(context, arrivalTimeComparator)
            setHasFixedSize(false)
        }

        bottomSheetNextStopsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NextStopsAdapter(context, stopArrivalTimeComparator)
            setHasFixedSize(false)
        }
    }

    override fun onNextArrivalListItemClick(arrivalItem: Arrival) {
        Log.d(TAG, "onNextArrivalListItemClick()")
//        val id = "${arrivalItem.line}/${arrivalItem.brigade}"
//        viewModel.setSelectedVehicle(id)
    }
}