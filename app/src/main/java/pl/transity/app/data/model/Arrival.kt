package pl.transity.app.data.model

import android.graphics.Color
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import com.google.gson.annotations.Expose
import pl.transity.app.utilities.TimestampUtils
import java.sql.Timestamp

class Arrival(
        @Expose val type: Int,
        @Expose val line: String,
        @Expose val brigade: String,
        @Expose val source: String,
        @Expose val destination: String,
        @Expose val arrival: Timestamp,
        @Expose val departure: Timestamp,
        @Expose val pickup: String,
        @Expose val bg: String,
        @Expose val fg: String
) : SortedListAdapter.ViewModel {

    val time: String get() = arrival.toString().subSequence(11, 16).toString()
    val eta: String get() = TimestampUtils.diffMin(arrival).toString()
    val bgColor: Int get() = Color.parseColor("#$bg")
    val fgColor: Int get() = Color.parseColor("#$fg")

    override fun <T : Any?> isSameModelAs(item: T): Boolean {
        if (item is Arrival) {
            val arrival = item as Arrival
            return line == arrival.line && brigade == arrival.brigade
        }
        return false
    }

    override fun <T : Any?> isContentTheSameAs(item: T): Boolean {
        if (item is Arrival) {
            val arrival = item as Arrival

            return this.arrival == arrival.arrival
        }
        return false
    }
}