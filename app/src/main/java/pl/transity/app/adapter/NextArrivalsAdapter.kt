package pl.transity.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import pl.transity.app.R
import pl.transity.app.constants.RouteType
import pl.transity.app.data.model.Arrival
import pl.transity.app.data.model.StopLine
import pl.transity.app.databinding.StopInfoNextArrivalBinding

class NextArrivalsAdapter(
        context: Context,
        comparator: Comparator<Arrival>
) : SortedListAdapter<Arrival>(context, Arrival::class.java, comparator) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out Arrival> {
        val binding = StopInfoNextArrivalBinding.inflate(inflater, parent, false)
        return NextArrivalViewHolder(binding)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("vehicleIcon")
        fun setVehicleIcon(imageView: ImageView, type: Int) {
            val iconRes = when (type) {
                RouteType.BUS -> R.drawable.ic_bus_gray_24dp
                RouteType.TRAM -> R.drawable.ic_tram_gray_24dp
                RouteType.RAIL -> R.drawable.ic_train_gray_24dp
                else -> R.drawable.ic_bus_gray_24dp
            }
            imageView.setImageResource(iconRes)
        }
    }

    inner class NextArrivalViewHolder(
            private val binding: StopInfoNextArrivalBinding
    ) : SortedListAdapter.ViewHolder<Arrival>(binding.root) {

        override fun performBind(arrival: Arrival) {
            binding.arrival = arrival
        }
    }
}