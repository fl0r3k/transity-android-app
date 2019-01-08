package pl.transity.app.utilities

import pl.transity.app.data.model.StopArrival

object DummyDataProvider {

    fun getStopsArrivals() : List<StopArrival>{
        val stopsArrivals = mutableListOf<StopArrival>()

//        stopsArrivals.add(StopArrival("999990","Czumy 02", "ul. Powstańców Śląskich","18:30","za 5 minut"))
//        stopsArrivals.add(StopArrival("999991","Ratusz Bemowo 01", "ul. Powstańców Śląskich","18:35","za 10 minut"))
//        stopsArrivals.add(StopArrival("999992","Radiowa 02", "ul. Radiowa","18:40","za 15 minut"))
//        stopsArrivals.add(StopArrival("999993","Wrocławska 01", "ul. Wrocławska","18:45","za 20 minut"))

        return stopsArrivals
    }
}