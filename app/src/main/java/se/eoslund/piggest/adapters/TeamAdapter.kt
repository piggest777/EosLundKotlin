package se.eoslund.piggest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import se.eoslund.piggest.R
import se.eoslund.piggest.controller.App
import se.eoslund.piggest.model.PlayerRO
import se.eoslund.piggest.services.DataService

class TeamAdapter(private val players: MutableList<PlayerRO>, private val itemClick: (PlayerRO)->Unit): RecyclerView.Adapter<TeamAdapter.PlayerHolder>(){


    inner class PlayerHolder(itemView: View, val itemClick: (PlayerRO) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val playerImage =  itemView.findViewById<ImageView>(R.id.player_image_view)
        private val playerName: TextView = itemView.findViewById(R.id.news_title)
        private val playerNumber: TextView = itemView.findViewById(R.id.news_date)
        private val playerPosition: TextView = itemView.findViewById(R.id.news_content)

        fun bindView(player: PlayerRO) {

            if (player.image != null) {
                val bmp = DataService.byteArrayToImage(player.image)
                playerImage.setImageBitmap(bmp)
            } else {
                playerImage.setImageResource(R.drawable.default_avatar)
            }
            playerName.text = player.name
            playerNumber.text = player.number.toString()
            playerPosition.text = player.position

            itemView.setOnClickListener{ itemClick(player) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerHolder {
        val view = LayoutInflater.from(App.instance)
            .inflate(R.layout.team_player_item, parent, false)
        return PlayerHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: PlayerHolder, position: Int) {
         holder.bindView(players[position])
    }

    override fun getItemCount(): Int {
        return  players.count()
    }
}