//package pl.transity.app.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter
//import pl.transity.app.data.model.Stop
//
//class NewFavoriteStopAdapter(
//        context: Context,
//        comparator: Comparator<Stop>,
//        private val addFavoriteStopClickListener : NewFavoriteStopAdapter.AddFavoriteStopClickListener
//) : SortedListAdapter<Stop>(context, Stop::class.java, comparator) {
//
//    interface AddFavoriteStopClickListener {
//        fun onAddFavoriteStopClick(id: String)
//    }
//
//    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out Stop> {
////        val binding = NewFavoriteStopListItemBinding.inflate(inflater, parent, false)
//        return FavoriteStopViewHolder(binding,addFavoriteStopClickListener)
//    }
//
//    inner class FavoriteStopViewHolder(
//            private val binding: NewFavoriteStopListItemBinding,
//            removeFavoriteStopClickListener : NewFavoriteStopAdapter.AddFavoriteStopClickListener
//    ) : SortedListAdapter.ViewHolder<Stop>(binding.root) {
//
////        init {
////            binding.listener = removeFavoriteStopClickListener
////        }
//
//        override fun performBind(stop: Stop) {
//            binding.stop = stop
//        }
//    }
//}