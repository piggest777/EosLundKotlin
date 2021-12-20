package se.eoslund.piggest.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import se.eoslund.piggest.Controller.App
import se.eoslund.piggest.Model.Game
import se.eoslund.piggest.R

class GameAdapter(val games:  List<Game>, val itemClick: (Game)-> Unit ): Adapter<GameAdapter.GameHolder>() {
    inner class GameHolder(val itemView: View, val intemClick: (Game) -> Unit): ViewHolder(itemView) {

        val homeTeamLogo = itemView.findViewById<ImageView>(R.id.list_home_team_logo_iv)
        val awayTeamLogo = itemView.findViewById<ImageView>(R.id.list_away_team_logo_iv)
        val scoresTV = itemView.findViewById<TextView>(R.id.game_list_item_scores)
        val resultDateTV = itemView.findViewById<TextView>(R.id.game_list_item_scores_date)
        val homeTeamNameTV = itemView.findViewById<TextView>(R.id.game_list_home_team_name_tv)
        val awayTeamNameTV = itemView.findViewById<TextView>(R.id.game_list_item_away_team_name_tv)
        val matchDateTV = itemView.findViewById<TextView>(R.id.game_list_item_date_tv)
        val matchTimeTV = itemView.findViewById<TextView>(R.id.game_list_item_time)
        val homeWinnerArrow = itemView.findViewById<ImageView>(R.id.game_list_item_home_winner_arrow)
        val awayWinnerArrow = itemView.findViewById<ImageView>(R.id.game_list_item_guest_winner_arrow)

        fun bindGame(game: Game) {




            itemView.setOnClickListener{itemClick(game)}
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