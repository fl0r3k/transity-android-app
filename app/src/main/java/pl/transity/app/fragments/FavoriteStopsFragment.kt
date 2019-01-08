package pl.transity.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import pl.transity.app.R
import pl.transity.app.adapter.FavoriteStopsAdapter
import pl.transity.app.utilities.Injection
import pl.transity.app.viewmodels.FavoritesActivityViewModel
import kotlinx.android.synthetic.main.fragment_favorite_stops.*

class FavoriteStopsFragment : Fragment() {

    private lateinit var viewModel: FavoritesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factoryViewModelFactory = Injection.provideFavoritesActivityViewModelFactory(context!!)
        viewModel = activity?.run {
            ViewModelProviders.of(this, factoryViewModelFactory).get(FavoritesActivityViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_stops, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupList()
    }

    private fun setupList() {
        val favoriteStopsLayoutManager = LinearLayoutManager(context)
        favoriteStopsList.apply {
            this.layoutManager = favoriteStopsLayoutManager
            adapter = FavoriteStopsAdapter(emptyList())
            setHasFixedSize(true)
        }

        viewModel.favoriteStops.observe(this, Observer { favoriteStops ->
            Log.d("FavoriteStopsFragment","favoriteStops.observer")
            Log.d("FavoriteStopsFragment","$favoriteStops")
            val newAdapter = FavoriteStopsAdapter(favoriteStops)
            favoriteStopsList.swapAdapter(newAdapter, true)
        })
    }
}