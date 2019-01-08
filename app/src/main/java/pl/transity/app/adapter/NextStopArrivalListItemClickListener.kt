package pl.transity.app.adapter

import pl.transity.app.data.model.StopArrival

interface NextStopArrivalListItemClickListener {

    fun onNextStopArrivalListItemClick(stopItem: StopArrival)

}