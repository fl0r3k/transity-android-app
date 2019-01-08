package pl.transity.app.adapter

import pl.transity.app.data.model.Arrival

interface NextArrivalListItemClickListener {

    fun onNextArrivalListItemClick(arrivalItem: Arrival)

}