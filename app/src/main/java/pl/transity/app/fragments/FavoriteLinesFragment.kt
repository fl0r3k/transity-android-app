package pl.transity.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite_lines.*
import pl.transity.app.R
import pl.transity.app.adapter.FavoriteLinesAdapter
import pl.transity.app.data.model.FavoriteLine
import pl.transity.app.utilities.Injection
import pl.transity.app.viewmodels.FavoritesActivityViewModel


class FavoriteLinesFragment : Fragment(), FavoriteLinesAdapter.RemoveFavoriteLineClickListener {

    private lateinit var viewModel: FavoritesActivityViewModel

    private val linesComparator = Comparator<FavoriteLine> { a, b -> a.line.compareTo(b.line) }

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

        val removeFavoriteLineClickListener = this as FavoriteLinesAdapter.RemoveFavoriteLineClickListener
        favoriteLinesList.apply {
            this.layoutManager = LinearLayoutManager(context)
            adapter = FavoriteLinesAdapter(context, linesComparator, removeFavoriteLineClickListener)
            setHasFixedSize(true)
        }

        viewModel.favoriteLines.observe(this, Observer { favoriteLines ->
            (favoriteLinesList.adapter as FavoriteLinesAdapter).edit()
                    .replaceAll(favoriteLines).commit()
        })
    }

    override fun onRemoveFavoriteLineClick(line: String) {
        viewModel.removeLineFromFavorites(line)
    }
}