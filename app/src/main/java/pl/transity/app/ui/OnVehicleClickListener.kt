package pl.transity.app.ui

import pl.transity.app.data.model.Vehicle

interface OnVehicleClickListener {
    fun onVehicleClicked(vehicle : Vehicle?)
}