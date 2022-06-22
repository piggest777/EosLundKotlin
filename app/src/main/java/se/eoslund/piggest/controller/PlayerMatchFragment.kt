package se.eoslund.piggest.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.MatchPlayerRecyclerViewAdapter
import se.eoslund.piggest.model.PlayerRO
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE

/**
 * A fragment representing a list of Items.
 */
class PlayerMatchFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_match_player_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val league = arguments?.getString(TEAM_LEAGUE)

                val games = league?.let { PlayerRO.getPlayersFromLeague(it) }

                if (games != null) {
                   adapter = MatchPlayerRecyclerViewAdapter(games)
                }

              //  adapter = MatchPlayerRecyclerViewAdapter(games)
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlayerMatchFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}