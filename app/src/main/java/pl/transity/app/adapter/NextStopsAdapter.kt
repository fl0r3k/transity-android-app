package pl.transity.app.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
import pl.transity.app.data.model.StopArrival
import pl.transity.app.databinding.VehicleInfoNextStopBinding

class NextStopsAdapter(
        context: Context,
        comparator: Comparator<StopArrival>
) : SortedListAdapter<StopArrival>(context, StopArrival::class.java, comparator) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): SortedListAdapter.ViewHolder<out StopArrival> {
        val binding = VehicleInfoNextStopBinding.inflate(inflater, parent, false)
        return NextStopViewHolder(binding)
    }

    inner class NextStopViewHolder(
            private val binding: VehicleInfoNextStopBinding
    ) : SortedListAdapter.ViewHolder<StopArrival>(binding.root) {

        override fun performBind(stopArrival: StopArrival) {
            binding.stopArrival = stopArrival
        }

//        private val name: TextView = itemView.findViewById(R.id.stop_name)
//        private val status: TextView = itemView.findViewById(R.id.stop_status)
//        private val time: TextView = itemView.findViewById(R.id.stop_time_timetable)
//        private val distance: TextView = itemView.findViewById(R.id.stop_distance_value)
//        private val eta: TextView = itemView.findViewById(R.id.stop_time_eta_value)

//        fun bind(stopArrival: StopArrival) {
//            name.text = stopArrival.name
//            time.text = stopArrival.arrival.toString().subSequence(11, 16)
//            distance.text = "${stopArrival.distance.toInt()} m"
//            eta.text = TimestampUtils.diffMin(stopArrival.arrival).toString()
//        }
    }
}