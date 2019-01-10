package pl.transity.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import pl.transity.app.data.model.FavoriteLine
import pl.transity.app.databinding.FavoriteLineListItemBinding


class FavoriteLinesAdapter(
        context: Context,
        comparator: Comparator<FavoriteLine>,
        private val removeFavoriteLineClickListener: FavoriteLinesAdapter.RemoveFavoriteLineClickListener
) : SortedListAdapter<FavoriteLine>(context, FavoriteLine::class.java, comparator) {

    interface RemoveFavoriteLineClickListener {
        fun onRemoveFavoriteLineClick(line: String)
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out FavoriteLine> {
        val binding = FavoriteLineListItemBinding.inflate(inflater, parent, false)
        return FavoriteLineViewHolder(binding, removeFavoriteLineClickListener)
    }


    inner class FavoriteLineViewHolder(
            private val binding: FavoriteLineListItemBinding,
            removeFavoriteLineClickListener: FavoriteLinesAdapter.RemoveFavoriteLineClickListener
    ) : SortedListAdapter.ViewHolder<FavoriteLine>(binding.root) {

        init {
            binding.listener = removeFavoriteLineClickListener
        }

        override fun performBind(favoriteLine: FavoriteLine) {
            binding.line = favoriteLine
        }

    }
}