package pl.transity.app.ui

import pl.transity.app.data.model.Stop

interface OnStopClickListener {
    fun onStopClicked(stop : Stop)
}