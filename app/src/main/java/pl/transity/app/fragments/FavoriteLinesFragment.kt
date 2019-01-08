package pl.transity.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import pl.transity.app.R
import pl.transity.app.adapter.FavoriteLinesAdapter
import pl.transity.app.utilities.Injection
import pl.transity.app.viewmodels.FavoritesActivityViewModel
import kotlinx.android.synthetic.main.fragment_favorite_lines.*
import pl.transity.app.adapter.decorator.GridItemDecorator



class FavoriteLinesFragment : Fragment() {

    private lateinit var viewModel: FavoritesActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factoryViewModelFactory = Injection.provideFavoritesActivityViewModelFactory(context!!)
        viewModel = activity?.run {
            ViewModelProviders.of(this, factoryViewModelFactory).get(FavoritesActivityViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_lines, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupList()
    }

    private fun setupList() {
        val favoriteLinesLayoutManager = GridLayoutManager(context,4)
        val decoration = GridItemDecorator(activity!!,16,4)
        favoriteLinesList.apply {
            this.layoutManager = favoriteLinesLayoutManager
            addItemDecoration(decoration)
            adapter = FavoriteLinesAdapter(emptyList())
            setHasFixedSize(true)

        }

        viewModel.favoriteLines.observe(this, Observer { favoriteLines ->
            val newAdapter = FavoriteLinesAdapter(favoriteLines)
            favoriteLinesList.swapAdapter(newAdapter, true)
        })
    }
}