package pl.transity.app.ui

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import pl.transity.app.AppExecutors
import pl.transity.app.R
import pl.transity.app.data.model.Vehicle
import pl.transity.app.utilities.Injection
import pl.transity.app.viewmodels.MainActivityViewModel

class VehicleInfoWindowFragment : androidx.fragment.app.Fragment() {

    companion object {
        private val TAG = VehicleInfoWindowFragment::class.simpleName
    }

    private lateinit var vehicleInfoWindow: View
//    private lateinit var vehicleInfoWindowProgressBar: ProgressBar

//    private lateinit var tvLine: TextView
//    private lateinit var tvBrigade: TextView
//    private lateinit var tvLat: TextView
//    private lateinit var tvLon: TextView
//    private lateinit var tvSpeed: TextView
//    private lateinit var tvBearing: TextView
//    private lateinit var tvOdometer: TextView
//    private lateinit var tvTime: TextView
//    private lateinit var tvTimestamp: TextView
//    private lateinit var tvRoute: TextView
    private lateinit var chronoTimeDiff: Chronometer
    private lateinit var chronoTimestampDiff: Chronometer
    private lateinit var vVehicleIcon : View


    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var executors: AppExecutors

    private lateinit var act: Activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView() - START")
        super.onCreateView(inflater, container, savedInstanceState)

        act = activity as Activity

        vehicleInfoWindow = inflater.inflate(R.layout.vehicle_info_window_fragment, container, false)
        vehicleInfoWindow.visibility = View.GONE

//        vehicleInfoWindowProgressBar = vehicleInfoWindow.findViewById(R.id.pb_vehicle_info_loading)

//        tvLine = vehicleInfoWindow.findViewById(R.id.tv_line)
//        tvBrigade = vehicleInfoWindow.findViewById(R.id.tv_brigade)
//        tvLat = vehicleInfoWindow.findViewById(R.id.tv_lat)
//        tvLon = vehicleInfoWindow.findViewById(R.id.tv_lon)
//        tvSpeed = vehicleInfoWindow.findViewById(R.id.tv_speed)
//        tvBearing = vehicleInfoWindow.findViewById(R.id.tv_bearing)
//        tvOdometer = vehicleInfoWindow.findViewById(R.id.tv_odometer)
//        tvTime = vehicleInfoWindow.findViewById(R.id.tv_time)
//        tvTimestamp = vehicleInfoWindow.findViewById(R.id.tv_timestamp)
//        tvRoute = vehicleInfoWindow.findViewById(R.id.tv_route)

        vVehicleIcon = vehicleInfoWindow.findViewById(R.id.v_bus_icon_on_progress_bar)

        chronoTimeDiff = vehicleInfoWindow.findViewById(R.id.chronometer_time_diff)
        chronoTimeDiff.onChronometerTickListener = Chronometer.OnChronometerTickListener { chronometer ->
            val deltaTime = (SystemClock.elapsedRealtime() - chronometer.base)/1000
            chronometer.text = "$deltaTime s"
        }

        chronoTimestampDiff = vehicleInfoWindow.findViewById(R.id.chronometer_timestamp_diff)
        chronoTimestampDiff.onChronometerTickListener = Chronometer.OnChronometerTickListener { chronometer ->
            val deltaTime = (SystemClock.elapsedRealtime() - chronometer.base)/1000
            chronometer.text = "$deltaTime s"
        }


        Log.d(TAG, "onCreateView() - END")
        return vehicleInfoWindow
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated() - START")
        super.onActivityCreated(savedInstanceState)
        executors = Injection.provideAppExecutors(act)
        setupViewModels()
        Log.d(TAG, "onActivityCreated() - END")
    }

    private fun setupViewModels() {
        Log.d(TAG, "setupViewModels() - START")
        setupVehicleViewModel()
        Log.d(TAG, "setupViewModels() - END")
    }

    private fun setupVehicleViewModel() {
        Log.d(TAG, "setupVehicleViewModel() - START")

        val factoryViewModelFactory = Injection.provideMainActivityViewModelFactory(act)

        mainActivityViewModel = ViewModelProviders
                .of(this, factoryViewModelFactory)
                .get(MainActivityViewModel::class.java)

//        mainActivityViewModel.selectedVehicleId.observe(this, Observer<String> { vehicleId ->
//            Log.d(TAG, "selectedVehicleId observer: $vehicleId")
//            if (vehicleId == null) {
//                vehicleInfoWindow.visibility = View.GONE
//            } else {
//                vehicleInfoWindowProgressBar.visibility = View.VISIBLE
//                vehicleInfoWindow.visibility = View.VISIBLE
//            }
//
//        })

//        mainActivityViewModel.selectedVehicle.observe(this, Observer<Vehicle> { vehicle ->
//            Log.d(TAG, "Selected Vehicle Observer $vehicle")
//            if (vehicle == null) vehicleInfoWindow.visibility = View.GONE
//            else {
//                infoWindowUpdater(vehicle)
////                vehicleInfoWindowProgressBar.visibility = View.GONE
//                vehicleInfoWindow.visibility = View.VISIBLE
//            }
//        })

        Log.d(TAG, "setupVehicleViewModel() - END")
    }

    private fun infoWindowUpdater(v: Vehicle) {
        Log.d(TAG, "infoWindowUpdater() - START")
        val ctm = System.currentTimeMillis()
        val timeDiff = ctm - v.time.time
        val timestampDiff = ctm - v.timestamp.time
        Log.d(TAG,"timeDiff: " + timeDiff.toString())
        Log.d(TAG,"timestampDiff: " + timestampDiff.toString())

//        tvLine.text = v.line
//        tvBrigade.text = v.brigade
//        tvLat.text = v.lat.toString() + " N"
//        tvLon.text = v.lon.toString() + " E"
//        tvSpeed.text = v.speed.toString() + " km/h"
//        tvBearing.text = v.bearing.toString() + resources.getString(R.string.symbol_degrees)
//        tvOdometer.text = v.odometer.toString() + " m"
//        tvTime.text = v.time.toString().substring(11)
//        tvTimestamp.text = v.timestamp.toString().substring(11)
//        tvRoute.text = v.route

        val ert = SystemClock.elapsedRealtime()

        chronoTimeDiff.base = ert - timeDiff
        chronoTimeDiff.text = "${timeDiff/1000} s"
        chronoTimeDiff.start()

        chronoTimestampDiff.base = ert - timestampDiff
        chronoTimestampDiff.text = "${timestampDiff/1000} s"
        chronoTimestampDiff.start()

//        vehicleInfoWindowProgressBar.visibility = View.GONE
        Log.d(TAG, "infoWindowUpdater() - END")
    }
}