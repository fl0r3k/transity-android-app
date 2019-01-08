package pl.transity.app.data.model

import android.graphics.Color
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import com.google.gson.annotations.Expose

class StopLine(
        @Expose val line: String,
        @Expose val type: Int,
        @Expose val bg: String,
        @Expose val fg: String
) : SortedListAdapter.ViewModel {

    companion object {
        const val TRAM = 0
        const val BUS = 3
    }

    val bgColor: Int get() = Color.parseColor("#$bg")
    val fgColor: Int get() = Color.parseColor("#$fg")

    override fun toString(): String {
        return "($line,$type,$bg,$fg)"
    }

    override fun <T : Any?> isSameModelAs(item: T): Boolean {
        if (item is StopLine) {
            val stopLine = item as StopLine
            return stopLine.line == line
        }
        return false
    }

    override fun <T : Any?> isContentTheSameAs(item: T): Boolean {
        if (item is StopLine) {
            val stopLine = item as StopLine
            return line == stopLine.line
        }
        return false
    }

}