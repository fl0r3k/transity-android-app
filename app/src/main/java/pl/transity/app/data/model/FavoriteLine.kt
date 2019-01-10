package pl.transity.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter


@Entity(tableName = "favourite_lines")
data class FavoriteLine(
        @PrimaryKey val line: String
) : SortedListAdapter.ViewModel {

    override fun <T : Any?> isContentTheSameAs(item: T): Boolean {
        if (item is Stop) {
            val fLine = item as FavoriteLine
            return fLine.line == line
        }
        return false
    }

    override fun <T : Any?> isSameModelAs(item: T): Boolean {
        if (item is Stop) {
            val fLine = item as FavoriteLine
            return fLine.line == line
        }
        return false
    }

}