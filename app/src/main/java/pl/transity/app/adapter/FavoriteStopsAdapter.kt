package pl.transity.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import pl.transity.app.data.model.Stop
import pl.transity.app.databinding.FavoriteStopListItemBinding


class FavoriteStopsAdapter(
        context: Context,
        comparator: Comparator<Stop>,
        private val removeFavoriteStopClickListener : FavoriteStopsAdapter.RemoveFavoriteStopClickListener
) : SortedListAdapter<Stop>(context, Stop::class.java, comparator) {

    interface RemoveFavoriteStopClickListener {
        fun onRemoveFavoriteStopClick(id: String)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out Stop> {
        val binding = FavoriteStopListItemBinding.inflate(inflater, parent, false)
        return FavoriteStopViewHolder(binding,removeFavoriteStopClickListener)
    }

    inner class FavoriteStopViewHolder(
            private val binding: FavoriteStopListItemBinding,
            removeFavoriteStopClickListener : FavoriteStopsAdapter.RemoveFavoriteStopClickListener
    ) : SortedListAdapter.ViewHolder<Stop>(binding.root) {

        init {
            binding.listener = removeFavoriteStopClickListener
        }

        override fun performBind(stop: Stop) {
            binding.stop = stop
        }
    }
}