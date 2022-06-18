package se.eoslund.piggest.controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.TabsPagerAdapter
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.model.TeamRO
import se.eoslund.piggest.services.DataService
import se.eoslund.piggest.services.DataService.toSnakeCase
import se.eoslund.piggest.services.DateFormat
import se.eoslund.piggest.services.DateFormatter
import se.eoslund.piggest.utilites.Constants
import java.util.*

class MatchActivity : AppCompatActivity() {

    lateinit var game: Game


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val tabViewPagerAdapter = findViewById<ViewPager2>(R.id.tabs_viewpager)

        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        tabLayout.tabTextColors = ContextCompat.getColorStateList(this, android.R.color.white)

        game = intent.getSerializableExtra("game") as Game

        val placeTextView = findViewById<TextView>(R.id.match_place)
        val dateTextView = findViewById<TextView>(R.id.match_date_tv)
        val homeLogo = findViewById<ImageView>(R.id.match_home_logo)
        val awayLogo = findViewById<ImageView>(R.id.match_away_logo)
        val homeNameTextView = findViewById<TextView>(R.id.match_home_name)
        val awayNameTextView = findViewById<TextView>(R.id.match_away_name)
        val scoresTextView = findViewById<TextView>(R.id.match_scores_or_time_tv)

        val lsTeam: TeamRO
        val rsTeam: TeamRO

        if (game.isHomeGame) {
            lsTeam = TeamRO(Constants.EOS_TEAM.id, Constants.EOS_TEAM.name, Constants.EOS_TEAM.city, Constants.EOS_TEAM.homeArena, Constants.EOS_TEAM.logoPath)
            rsTeam = TeamRO.getTeamById(game.rsTeamCode)
        } else {
            rsTeam = TeamRO(Constants.EOS_TEAM.id, Constants.EOS_TEAM.name, Constants.EOS_TEAM.city, Constants.EOS_TEAM.homeArena, Constants.EOS_TEAM.logoPath)
            lsTeam = TeamRO.getTeamById(game.lsTeamCode)
        }

        val curDate = Date()
        val lsTeamLogo = DataService.setUpTeamLogo(lsTeam.logoPathName.toSnakeCase())
        val rsTeamLogo = DataService.setUpTeamLogo(rsTeam.logoPathName.toSnakeCase())

        val matchDate =  DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.DAY_AND_MONTH)
        val matchTime = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.TIME)

        placeTextView.text = App.instance.getString(R.string.game_place, lsTeam.city, lsTeam.homeArena)
        dateTextView.text = App.instance.getString(R.string.match_time, matchDate, matchTime)
        homeLogo.setImageDrawable(lsTeamLogo)
        awayLogo.setImageDrawable(rsTeamLogo)
        homeNameTextView.text = lsTeam.name
        awayNameTextView.text = rsTeam.name
        scoresTextView.text = App.instance.getString(R.string.scores, game.lsScores.toInt(), game.rsScores.toInt())

        val numberOfTabs = 3
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.isInlineLabel = true
        val adapter = TabsPagerAdapter(supportFragmentManager, lifecycle, numberOfTabs)
        tabViewPagerAdapter.adapter = adapter
        tabViewPagerAdapter.isUserInputEnabled = true

        TabLayoutMediator(tabLayout, tabViewPagerAdapter) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Music"
                    tab.setIcon(R.drawable.games)
                }
                1 -> {
                    tab.text = "Movies"
                    tab.setIcon(R.drawable.games)

                }
                2 -> {
                    tab.text = "Books"
                    tab.setIcon(R.drawable.games)
                }

            }
            tab.icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.WHITE,
                    BlendModeCompat.SRC_ATOP
                )
        }.attach()


    }
}