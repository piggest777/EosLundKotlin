package se.eoslund.piggest.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import se.eoslund.piggest.R
import se.eoslund.piggest.model.PlayerRO
import se.eoslund.piggest.services.DataService
import se.eoslund.piggest.services.DateFormatter

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerId: String
    lateinit var player:PlayerRO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerId = intent.getStringExtra("playerID")!!
        player = PlayerRO.findPlayerById(playerId)

        val playerImageViewIV = findViewById<ImageView>(R.id.player_image)
        val numberTV = findViewById<TextView>(R.id.player_number)
        val nameTV = findViewById<TextView>(R.id.player_name)
        val ageTV = findViewById<AppCompatTextView>(R.id.player_age_txt)
        val heightTV = findViewById<AppCompatTextView>(R.id.player_height_txt)
        val positionTV = findViewById<AppCompatTextView>(R.id.player_position_txt)
        val originalClubTV= findViewById<AppCompatTextView>(R.id.player_original_club_txt)
        val inEosFromTV = findViewById<AppCompatTextView>(R.id.player_ineosfrom_txt)
        val nationalityTV = findViewById<AppCompatTextView>(R.id.player_nationality_txt)

        Log.d("DEBUG_REALM", "player name is : ${player.name}")

        if (player.image != null) {
            val bmp = DataService.byteArrayToImage(player.image)
            playerImageViewIV.setImageBitmap(bmp)
        } else {
            playerImageViewIV.setImageResource(R.drawable.default_avatar)
        }
//            playerImageViewIV.setImageBitmap(DataService.byteArrayToImage(player.image))
        numberTV.text = player.number.toString()
        nameTV.text = player.name
        if(player.dayOfBirth != null) {
            ageTV.text  = DateFormatter.getAge(player.dayOfBirth!!)
        } else {
            ageTV.text = ""
        }
        heightTV.text = player.height ?: ""
        positionTV.text = player.position
        originalClubTV.text = player.originalClub ?: ""
        inEosFromTV.text = player.inEosFrom ?: ""
        nationalityTV.text = player.nationality ?: ""
    }

    fun arrowOnClick(view: View) {
        finish()
    }
    fun backButtonPressed(view: View) {
        finish()
    }
}