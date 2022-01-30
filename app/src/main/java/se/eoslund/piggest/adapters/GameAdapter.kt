package se.eoslund.piggest.adapters

import android.provider.Settings.Global.getString
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
import se.eoslund.piggest.services.DataService.getResource
import se.eoslund.piggest.services.DateFormat
import se.eoslund.piggest.services.DateFormatter
import se.eoslund.piggest.utilites.Constants.EOS_TEAM
import java.util.*

class GameAdapter(private val games:  MutableList<Game>, val itemClick: (Game)-> Unit ): RecyclerView.Adapter<GameAdapter.GameHolder>() {

    inner class GameHolder(val itemView: View, val itemClick: (Game) -> Unit): ViewHolder(itemView) {

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
            //todo: think about left/right side func
            val oppTeam =  TeamRO.getTeamById(game.oppositeTeamCode)

            val curDate = Date()
            val leftSideScores: Int
            val rightSideScores: Int

            val oppTeamLogo = App.instance.getResource(oppTeam.logoPathName)
            val defaultTeamLogo = R.drawable.default_image_logo

            resultDateTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.DAY_AND_MONTH)
            matchDateTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.DATE)
            matchTimeTV.text = DateFormatter.getFormattedDate(game.gameDateAndTime, DateFormat.TIME)

            if(game.isHomeGame) {
                homeTeamLogo.setImageResource(R.drawable.eos_logo)
                //todo: Rewrite image assign way
               if(oppTeamLogo == null){
                   awayTeamLogo.setImageResource(defaultTeamLogo)
                }else {
                    awayTeamLogo.setImageDrawable(oppTeamLogo)
               }
                awayTeamLogo.setImageResource(R.drawable.default_image_logo)
                leftSideScores = game.eosScore.toInt()
                rightSideScores = game.oppositeTeamScore.toInt()
                scoresTV.text = App.instance.getString(R.string.scores,leftSideScores, rightSideScores)
                homeTeamNameTV.text = EOS_TEAM.name
                awayTeamNameTV.text = oppTeam.name
                placeTV.text = App.instance.getString(R.string.game_place, EOS_TEAM.city, EOS_TEAM.homeArena)

                if (curDate>game.gameDateAndTime && game.eosScore > game.oppositeTeamScore) {
                    homeWinnerArrow.visibility = VISIBLE
                    awayWinnerArrow.visibility = INVISIBLE
                } else if (curDate>game.gameDateAndTime && game.eosScore < game.oppositeTeamScore) {
                    homeWinnerArrow.visibility = INVISIBLE
                    awayWinnerArrow.visibility = VISIBLE
                }

            } else {
                if(oppTeamLogo == null){
                    homeTeamLogo.setImageResource(defaultTeamLogo)
                }else {
                    homeTeamLogo.setImageDrawable(oppTeamLogo)
                }
                awayTeamLogo.setImageResource(R.drawable.eos_logo)

                leftSideScores = game.oppositeTeamScore.toInt()
                rightSideScores = game.eosScore.toInt()
                scoresTV.text = App.instance.getString(R.string.scores, leftSideScores, rightSideScores)
                homeTeamNameTV.text = oppTeam.name
                awayTeamNameTV.text = EOS_TEAM.name
                placeTV.text = App.instance.getString(R.string.game_place, oppTeam.city, oppTeam.homeArena)

                if (curDate>game.gameDateAndTime && game.eosScore < game.oppositeTeamScore) {
                    homeWinnerArrow.visibility = VISIBLE
                    awayWinnerArrow.visibility = INVISIBLE
                } else if (curDate>game.gameDateAndTime && game.eosScore > game.oppositeTeamScore) {
                    homeWinnerArrow.visibility = INVISIBLE
                    awayWinnerArrow.visibility = VISIBLE
                }
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

            when {
                leftSideScores > rightSideScores -> {
                    homeWinnerArrow.visibility = VISIBLE
                    awayWinnerArrow.visibility = INVISIBLE
                }
                leftSideScores < rightSideScores -> {
                    homeWinnerArrow.visibility = INVISIBLE
                    awayWinnerArrow.visibility = VISIBLE
                }
                else -> {
                    homeWinnerArrow.visibility = INVISIBLE
                    awayWinnerArrow.visibility = VISIBLE
                }
            }
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