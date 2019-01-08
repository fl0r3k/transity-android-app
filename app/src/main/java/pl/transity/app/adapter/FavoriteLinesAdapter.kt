package pl.transity.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.transity.app.data.model.FavoriteLine
import pl.transity.app.databinding.FavoriteLineListItemBinding


class FavoriteLinesAdapter(private val favoriteLines: List<FavoriteLine>) : RecyclerView.Adapter<FavoriteLinesAdapter.FavoriteLineViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FavoriteLineViewHolder {
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val binding = FavoriteLineListItemBinding.inflate(inflater, viewGroup, false)
        return FavoriteLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteLineViewHolder, position: Int) {
        val favoriteLine = favoriteLines[position]
        holder.bind(favoriteLine)
    }

    override fun getItemCount() = favoriteLines.size


    inner class FavoriteLineViewHolder(private val binding: FavoriteLineListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favoriteLine: FavoriteLine) {
            binding.line = favoriteLine
        }
    }
}