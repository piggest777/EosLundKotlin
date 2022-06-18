package se.eoslund.piggest.adapters

import android.graphics.drawable.Drawable
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import se.eoslund.piggest.controller.App
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.model.TeamRO
import se.eoslund.piggest.R
import se.eoslund.piggest.services.DataService
import se.eoslund.piggest.services.DataService.getResource
import se.eoslund.piggest.services.DataService.toSnakeCase
import se.eoslund.piggest.services.DateFormat
import se.eoslund.piggest.services.DateFormatter
import se.eoslund.piggest.utilites.Constants.EOS_TEAM
import java.util.*

class GameAdapter(private val games:  MutableList<Game>, val itemClick: (Game)-> Unit ): RecyclerView.Adapter<GameAdapter.GameHolder>() {

    inner class GameHolder( itemView: View, val itemClick: (Game) -> Unit): ViewHolder(itemView) {

        private val homeTeamLogo: ImageView = itemView.findViewById(R.id.list_home_team_logo_iv)
        private val awayTeamLogo: ImageView = itemView.findViewById(R.id.list_away_team_logo_iv)
        private val scoresTV: TextView = itemView.findViewById(R.id.game_list_item_scores)
        private var resultDateTV: TextView = itemView.findViewById(R.id.game_list_item_scores_date)
        private val homeTeamNameTV: TextView = itemView.findViewById(R.id.game_list_home_team_name_tv)
        private val awayTeamNameTV: TextView = itemView.findViewById(R.id.game_list_item_away_team_name_tv)
        private val matchDateTV: TextView = itemView.findViewById(R.id.game_list_item_date_tv)
        private val matchTimeTV: TextView = itemView.findViewById(R.id.game_list_item_time)
        private val placeTV: TextView = itemView.findViewById(R.id.game_list_item_place)
        private val homeWinnerArrow: ImageView = itemView.findViewById(R.id.game_list_item_home_winner_arrow)
        private val awayWinnerArrow: ImageView = itemView.findViewById(R.id.game_list_item_guest_winner_arrow)

        fun bindGame(game: Game) {

            val lsTeam: TeamRO
            val rsTeam: TeamRO

            if (game.isHomeGame) {
                lsTeam = TeamRO(EOS_TEAM.id, EOS_TEAM.name, EOS_TEAM.city, EOS_TEAM.homeArena, EOS_TEAM.logoPath)
                rsTeam = TeamRO.getTeamById(game.rsTeamCode)
            } else {
                rsTeam = TeamRO(EOS_TEAM.id, EOS_TEAM.name, EOS_TEAM.city, EOS_TEAM.homeArena, EOS_TEAM.logoPath)
                lsTeam = TeamRO.getTeamById(game.lsTeamCode)
            }


            val curDate = Date()
            val lsTeamLogo = DataService.setUpTeamLogo(lsTeam.logoPathName.toSnakeCase())
            val rsTeamLogo = DataService.setUpTeamLogo(rsTeam.logoPathName.toSnakeCase())

            resultDateTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.DAY_AND_MONTH)
            matchDateTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.DATE)
            matchTimeTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.TIME)

            homeTeamLogo.setImageDrawable(lsTeamLogo)
            awayTeamLogo.setImageDrawable(rsTeamLogo)
            scoresTV.text = App.instance.getString(R.string.scores,game.lsScores, game.rsScores)
            homeTeamNameTV.text = lsTeam.name
            awayTeamNameTV.text = rsTeam.name
            placeTV.text = App.instance.getString(R.string.game_place, EOS_TEAM.city, EOS_TEAM.homeArena)

            if (curDate > game.gameDateAndTime && game.lsScores < game.rsScores) {
                homeWinnerArrow.visibility = VISIBLE
                awayWinnerArrow.visibility = INVISIBLE
            } else if (curDate > game.gameDateAndTime && game.lsScores > game.rsScores) {
                homeWinnerArrow.visibility = INVISIBLE
                awayWinnerArrow.visibility = VISIBLE
            }

            when {
                curDate > game.gameDateAndTime -> {
                    resultDateTV.visibility = VISIBLE
                    scoresTV.visibility = VISIBLE
                    matchTimeTV.visibility = INVISIBLE
                    matchDateTV.visibility = INVISIBLE
                }
                else -> {
                    resultDateTV.visibility = INVISIBLE
                    scoresTV.visibility = INVISIBLE
                    matchTimeTV.visibility = VISIBLE
                    matchDateTV.visibility = VISIBLE
                }
            }

//            when {
//                leftSideScores > rightSideScores -> {
//                    homeWinnerArrow.visibility = VISIBLE
//                    awayWinnerArrow.visibility = INVISIBLE
//                }
//                leftSideScores < rightSideScores -> {
//                    homeWinnerArrow.visibility = INVISIBLE
//                    awayWinnerArrow.visibility = VISIBLE
//                }
//                else -> {
//                    homeWinnerArrow.visibility = INVISIBLE
//                    awayWinnerArrow.visibility = VISIBLE
//                }
//            }
            itemView.setOnClickListener{ itemClick(game) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
        val view = LayoutInflater.from(App.instance)
            .inflate(R.layout.game_schedule_list_item, parent, false)
        return GameHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: GameHolder, position: Int) {
       holder.bindGame(games[position])
    }

    override fun getItemCount(): Int {
        return games.count()
    }
}