package pl.transity.app.viewmodels

import android.util.Log
import android.view.View
import androidx.databinding.*
import androidx.lifecycle.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import pl.transity.app.data.StopRepository
import pl.transity.app.data.VehicleRepository
import pl.transity.app.data.model.Stop
import pl.transity.app.data.model.StopArrival
import pl.transity.app.data.model.Vehicle
import pl.transity.app.data.network.Bounds
import pl.transity.app.utilities.TimestampUtils


@BindingAdapter("behavior_state")
fun setBottomSheetBehaviorState(view: View, @BottomSheetBehavior.State state: Int) {
    val viewBottomSheetBehavior = BottomSheetBehavior.from(view)
    viewBottomSheetBehavior.state = state
}


class MainActivityViewModel(
        private val stopRepository: StopRepository,
        private val vehicleRepository: VehicleRepository
) : ViewModel(), Observable {


    companion object {
        const val TAG = "MainActivityViewModel"
        const val STOPS_ZOOM_CUTOFF_MIN = 15f
        const val STOPS_GROUPS_ZOOM_CUTOFF_MIN = 12f
        const val STOPS_GROUPS_ZOOM_CUTOFF_MAX = 15f
        const val FAVORITE_STOPS_ZOOM_CUTOFF_MAX = 10f
        const val VEHICLES_ZOOM_CUTOFF_MAX = 14f
        const val FAVORITES_VEHICLES_ZOOM_CUTOFF_MAX = 10f
    }


    private val zoom = MutableLiveData<Float>().default(0f)
    private val stopsVisible = MutableLiveData<Boolean>().default(true)
    private val onlyFavoriteStops = MutableLiveData<Boolean>().default(false)
    private val vehiclesVisible = MutableLiveData<Boolean>().default(true)
    private val isStopSelected = MutableLiveData<Boolean>().default(false)

    private val selectedStop = MutableLiveData<Stop>()

    private val isVehicleSelected = MutableLiveData<Boolean>().default(false)
    fun isVehicleSelected(): LiveData<Boolean> {
        return isVehicleSelected
    }

    private val selectedVehicle = MutableLiveData<String>().default(null)
    fun getSelectedVehicle(): LiveData<String> {
        return selectedVehicle
    }

    val selectedVehicleData: LiveData<Vehicle> = Transformations.map(vehicleRepository.vehicle) {
        actionBarTitle.set("${it.line}  ${it.destination}")
        it
    }
//    val selectedVehicleRouteStops : LiveData<List<Stop>> = stopRepository.routeStops

    val stopsArrivals: LiveData<List<StopArrival>> = Transformations.map(vehicleRepository.stopsArrivals){
        val filteredList = mutableListOf<StopArrival>()
        for (sa in it)
            if (TimestampUtils.diff(sa.arrival) >= -120000)
                filteredList.add(sa)
        filteredList
    }

    private val isStopsLayerVisible = MediatorLiveData<Boolean>()
    private val isStopsGroupsLayerVisible = MediatorLiveData<Boolean>()
    private val isVehiclesLayerVisible = MediatorLiveData<Boolean>()
    private val isFavoriteStopsLayerVisible = MediatorLiveData<Boolean>()

    val isLocationEnabled = ObservableBoolean(false)
    val isActionBarVisible = ObservableBoolean(false)
    val actionBarTitle = ObservableField<String>("")
    val rightDrawerHeaderTitle = ObservableField<String>("Ogród Działkowy Im.Warneńczyka 01")
    val selectedStopType = ObservableInt(Stop.BUS)
    var bottomSheetLinesListVisible = false

    val bottomSheetBehaviorState = ObservableInt(BottomSheetBehavior.STATE_HIDDEN)
    val bottomSheetBehaviorPeekHeight = ObservableInt(224)

    @Bindable
    var isFavoriteSelected = false


    fun getStopsLayerVisibility(): LiveData<Boolean> {
        return isStopsLayerVisible
    }

    fun getStopsGroupsLayerVisibility(): LiveData<Boolean> {
        return isStopsGroupsLayerVisible
    }

    fun getFavoriteStopsLayerVisibility(): LiveData<Boolean> {
        return isFavoriteStopsLayerVisible
    }

    fun getVehiclesLayerVisibility(): LiveData<Boolean> {
        return isVehiclesLayerVisible
    }

    fun setSelectedVehicle(id: String) {
        clearSelectedStop()
        bottomSheetLinesListVisible = false

        val line = id.split('/')[0]

        selectedVehicle.value = id
        isVehicleSelected.value = true
        isFavoriteSelected = isVehicleFavorite(line)
        actionBarTitle.set(line)
        if (!isActionBarVisible.get()) isActionBarVisible.set(true)

        bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_COLLAPSED)

        vehicleRepository.fetchSelectedVehicleDetails(id)
        notifyChange()
    }

    fun setSelectedStop(stop: Stop) {
        clearSelectedVehicle()
        bottomSheetLinesListVisible = true

        stopRepository.fetchSelectedStopLines(stop.id)
        stopRepository.startFetchArrivals(stop.id)

        isStopSelected.value = true
        selectedStop.value = stop
        isFavoriteSelected = isStopFavorite(stop.id)
        actionBarTitle.set(stop.name)
        if (!isActionBarVisible.get()) isActionBarVisible.set(true)

        bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_COLLAPSED)

        notifyChange()
    }

    fun clearSelection() {
        clearSelectedVehicle()
        clearSelectedStop()
    }

    private fun clearSelectedVehicle() {
        isVehicleSelected.value = false
        selectedVehicle.value = null
        isActionBarVisible.set(false)
        bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_HIDDEN)
    }

    private fun clearSelectedStop() {
        isStopSelected.value = false
        selectedStop.value = null
    }

    private fun isStopFavorite(id: String): Boolean {
        Log.d(TAG, "Favorite stops list ${favoriteStopsIds.size}")
        return if (favoriteStopsIds.contains(id)) {
            Log.d(TAG, "Stop is favorite")
            true
        } else {
            Log.d(TAG, "Stop is not favorite")
            false
        }
    }

    private fun isVehicleFavorite(line: String): Boolean {
        Log.d(TAG, "Favorite vehicles list ${favoriteLines.size}")
        return if (favoriteLines.contains(line)) {
            Log.d(TAG, "Line is favorite")
            true
        } else {
            Log.d(TAG, "Line is not favorite")
            false
        }
    }

    fun addObjectToFavorites() {
        if (selectedStop.value != null) {
            stopRepository.addStopToFavorites(selectedStop.value!!.id)
        } else if (isVehicleSelected.value == true) {
            val line = selectedVehicle.value!!.split('/')[0]
            vehicleRepository.addLineToFavorites(line)
        }
        isFavoriteSelected = true
        notifyChange()
    }

    fun removeObjectFromFavorites() {
        if (selectedStop.value != null) {
            stopRepository.removeStopFromFavorites(selectedStop.value!!.id)
        } else if (isVehicleSelected.value == true) {
            val line = selectedVehicle.value!!.split('/')[0]
            vehicleRepository.removeLineFromFavorites(line)
        }
        isFavoriteSelected = false
        notifyChange()
    }

    private val favoriteStopsSubscription = stopRepository.favoriteStopsIds.subscribe { favoriteStopsIds ->
        Log.d(TAG, favoriteStopsIds.toString())
        this.favoriteStopsIds = favoriteStopsIds
    }

    private val favoriteLinesSubscription = vehicleRepository.favoriteLinesFlowable.subscribe { favoriteLines ->
        Log.d(TAG, favoriteLines.toString())
        this.favoriteLines = favoriteLines
    }

    fun disposeSubscriptions() {
        favoriteStopsSubscription.dispose()
        favoriteLinesSubscription.dispose()
    }

    init {
        isStopsLayerVisible.addSource(zoom) {
            isStopsLayerVisible.value = shouldShowStopsLayer()
        }
        isStopsLayerVisible.addSource(stopsVisible) {
            isStopsLayerVisible.value = shouldShowStopsLayer()
        }
        isStopsLayerVisible.addSource(isStopSelected) {
            isStopsLayerVisible.value = shouldShowStopsLayer()
        }
        isStopsLayerVisible.addSource(isVehicleSelected) {
            isStopsLayerVisible.value = shouldShowStopsLayer()
        }


        isStopsGroupsLayerVisible.addSource(zoom) {
            isStopsGroupsLayerVisible.value = shouldShowStopsGroupsLayer()
        }
        isStopsGroupsLayerVisible.addSource(stopsVisible) {
            isStopsGroupsLayerVisible.value = shouldShowStopsGroupsLayer()
        }
        isStopsGroupsLayerVisible.addSource(isStopSelected) {
            isStopsGroupsLayerVisible.value = shouldShowStopsGroupsLayer()
        }
        isStopsGroupsLayerVisible.addSource(isVehicleSelected) {
            isStopsGroupsLayerVisible.value = shouldShowStopsGroupsLayer()
        }


        isFavoriteStopsLayerVisible.addSource(zoom) {
            isFavoriteStopsLayerVisible.value = shouldShowFavoriteStopsLayer()
        }


        isVehiclesLayerVisible.addSource(zoom) {
            isVehiclesLayerVisible.value = shouldShowVehiclesLayer()
        }
        isVehiclesLayerVisible.addSource(vehiclesVisible) {
            isVehiclesLayerVisible.value = shouldShowVehiclesLayer()
        }
        isVehiclesLayerVisible.addSource(isStopSelected) {
            isVehiclesLayerVisible.value = shouldShowVehiclesLayer()
        }
//        isVehiclesLayerVisible.addSource(isVehicleSelected) {
//            isVehiclesLayerVisible.value = shouldShowVehiclesLayer()
//        }
    }

    private fun shouldShowStopsLayer(): Boolean {

        return ((zoom.value!! > STOPS_ZOOM_CUTOFF_MIN)
                && stopsVisible.value!!
                && !isVehicleSelected.value!!)
    }

    private fun shouldShowStopsGroupsLayer(): Boolean {

        return ((zoom.value!! > STOPS_GROUPS_ZOOM_CUTOFF_MIN)
                && (zoom.value!! < STOPS_GROUPS_ZOOM_CUTOFF_MAX)
                && stopsVisible.value!!
                && !isVehicleSelected.value!!)
    }

    private fun shouldShowFavoriteStopsLayer(): Boolean {
        return (zoom.value!! > FAVORITE_STOPS_ZOOM_CUTOFF_MAX)
    }

    private fun shouldShowVehiclesLayer(): Boolean {
        return ((zoom.value!! > VEHICLES_ZOOM_CUTOFF_MAX)
//                && !isVehicleSelected.value!!
                && vehiclesVisible.value!!)
    }


    fun setZoom(zoom: Float) {
        this.zoom.value = zoom
    }

    fun getStopsVisible(): LiveData<Boolean> {
        return stopsVisible
    }

    fun getIsStopSelected(): LiveData<Boolean> {
        return isStopSelected
    }


    //Stops fields
    val stops = stopRepository.stops
    private var favoriteStopsIds = emptyList<String>()

    val selectedStopLines = stopRepository.stopLines
    val arrivals = stopRepository.arrivals
    val stopsGroups = stopRepository.getAllStopsGroups()
    val favoriteStops = stopRepository.favoriteStops

    //Vehicles fields
    val vehicles = vehicleRepository.vehicles
    private var favoriteLines = emptyList<String>()

    fun startVehicleFetcher() {
        Log.d(TAG, "startVehicleFetcher()")
        vehicleRepository.startVehiclesFetcher()
    }

    fun stopVehicleFetcher() {
        Log.d(TAG, "stopVehicleFetcher()")
        vehicleRepository.stopVehiclesFetcher()
    }

    fun setVehicleFetchRegion(bounds: Bounds) {
        Log.d(TAG, "setVehicleFetchRegion()")
        vehicleRepository.setVehiclesFetchRegion(bounds)
    }

    //Stops methods
    fun setOnlyFavoriteVehicles(onlyFavorite: Boolean) {
        Log.d(TAG, "setOnlyFavoriteVehicles()")
        vehicleRepository.setOnlyFavoriteVehicles(onlyFavorite)
    }


    @Transient
    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }

    @Synchronized
    fun notifyChange() {
        Log.d(TAG, "NOTIFYING!!1")
        callbacks.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

}

private fun <T> MutableLiveData<T>.default(initialValue: T?): MutableLiveData<T> = apply { value = initialValue }
