package se.eoslund.piggest.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import se.eoslund.piggest.controller.DemoFragment
import se.eoslund.piggest.controller.MatchDescriptionFragment
import se.eoslund.piggest.controller.PlayerMatchFragment
import se.eoslund.piggest.controller.WebStatsFragment
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.utilites.Constants.GAME_DESCRIPTION
import se.eoslund.piggest.utilites.Constants.STATISTIC_LINK
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int,
                       private val match: Game
) : FragmentStateAdapter(fm, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                bundle.putString(STATISTIC_LINK, match.statsLink)
                val webStatsFragment = WebStatsFragment()
                webStatsFragment.arguments = bundle
                return webStatsFragment
            }
            1 -> {
                val bundle = Bundle()
                bundle.putString(TEAM_LEAGUE, match.teamLeague)
                val playersListFragment = PlayerMatchFragment()
                playersListFragment.arguments = bundle
                return playersListFragment
            }
            2 -> {
                val bundle = Bundle()
                bundle.putString(GAME_DESCRIPTION, match.gameDescription)
                val matchDescriptionFragment = MatchDescriptionFragment()
                matchDescriptionFragment.arguments = bundle
                return matchDescriptionFragment
            }
            else -> return DemoFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}