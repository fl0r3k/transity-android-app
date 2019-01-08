package pl.transity.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.transity.app.R
import pl.transity.app.data.model.Stop

class FavoriteStopsAdapter(private val favoriteStops: List<Stop>) : RecyclerView.Adapter<FavoriteStopsAdapter.FavoriteStopViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FavoriteStopViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.favorite_stop_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false
        val view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately)
        return FavoriteStopViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteStopViewHolder, position: Int) {
        val stopLine = favoriteStops[position]
        holder.bind(stopLine)
    }

    override fun getItemCount() = favoriteStops.size


    inner class FavoriteStopViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val stopName: TextView = itemView.findViewById(R.id.stop_name)

        fun bind(favoriteStop: Stop) {
            stopName.text = favoriteStop.name
        }
    }
}