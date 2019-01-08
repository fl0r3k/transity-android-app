package pl.transity.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import pl.transity.app.data.model.StopLine
import pl.transity.app.databinding.StopInfoLinesListIconBinding

class LinesIconsAdapter(
        context: Context,
        comparator: Comparator<StopLine>
) : SortedListAdapter<StopLine>(context, StopLine::class.java, comparator) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out StopLine> {
        val binding = StopInfoLinesListIconBinding.inflate(inflater, parent, false)
        return LineIconViewHolder(binding)
    }

    inner class LineIconViewHolder(
            private val binding: StopInfoLinesListIconBinding
    ) : SortedListAdapter.ViewHolder<StopLine>(binding.root) {

        override fun performBind(stopLine: StopLine) {
            binding.stopLine = stopLine
        }
    }
}