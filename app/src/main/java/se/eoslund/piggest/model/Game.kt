package se.eoslund.piggest.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import se.eoslund.piggest.utilites.Constants.EOS_PLAYERS
import se.eoslund.piggest.utilites.Constants.EOS_SCORES
import se.eoslund.piggest.utilites.Constants.GAME_COVER_URL
import se.eoslund.piggest.utilites.Constants.GAME_DATE_AND_TIME
import se.eoslund.piggest.utilites.Constants.GAME_DESCRIPTION
import se.eoslund.piggest.utilites.Constants.IS_HOME_GAME
import se.eoslund.piggest.utilites.Constants.OPPOSITE_TEAM_CODE
import se.eoslund.piggest.utilites.Constants.OPPOSITE_TEAM_PLAYERS
import se.eoslund.piggest.utilites.Constants.OPPOSITE_TEAM_SCORES
import se.eoslund.piggest.utilites.Constants.STATISTIC_LINK
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE
import java.io.Serializable
import java.util.*

public class Game(
    val lsTeamCode: String,
    val rsTeamCode: String,
    val lsScores: Long,
    val rsScores: Long,
    val isHomeGame: Boolean,
    val gameDateAndTime: Date,
    val teamLeague: String,
    val documentId: String,
    val eosPlayers: String?,
    val oppTeamPlayer: String?,
    val statsLink: String?,
    val gameDescription: String?,
    val gameCoverURL: String?
) : Serializable {
    companion object {

        fun parseGameFromSnapshot(snapshot: DocumentSnapshot): Game? {
            val data = snapshot.data ?: return null
            val id = snapshot.id
            val oppositeTeamCode = data[OPPOSITE_TEAM_CODE] as? String ?: "TEAM"
            val eosScore = data[EOS_SCORES] as? Long ?: 0
            val oppositeTeamScore = data[OPPOSITE_TEAM_SCORES] as? Long ?: 0
            val isHomeGame = data[IS_HOME_GAME] as? Boolean
            val gameDate = data[GAME_DATE_AND_TIME] as? Timestamp
            val teamLeague = data[TEAM_LEAGUE] as? String
            val eosPlayers: String? = data[EOS_PLAYERS] as? String
            val oppTeamPlayers = data[OPPOSITE_TEAM_PLAYERS] as? String
            val statsLink = data[STATISTIC_LINK] as? String
            val gameDesc = data[GAME_DESCRIPTION] as? String
            val gameCoverURL = data[GAME_COVER_URL] as? String

            val javaDate: Date? = gameDate?.toDate()

            if (isHomeGame != null && javaDate != null && teamLeague != null) {
                val lsTeamCode: String
                val lsScores: Long
                val rsTeamCode: String
                val rsScores: Long
                if(isHomeGame) {
                    lsTeamCode  = "EOS"
                    lsScores = eosScore
                    rsTeamCode = oppositeTeamCode
                    rsScores = oppositeTeamScore
                } else {
                    rsTeamCode  = "EOS"
                    rsScores = eosScore
                    lsTeamCode = oppositeTeamCode
                    lsScores = oppositeTeamScore
                }

                return Game(lsTeamCode,rsTeamCode,lsScores,rsScores, isHomeGame, javaDate,teamLeague, id, eosPlayers,oppTeamPlayers,statsLink,gameDesc,gameCoverURL)

            } else {
                return null
            }
        }

        fun parseGamesList(snapshot: QuerySnapshot): MutableList<Game> {
            val games = mutableListOf<Game>()

            snapshot.forEach {
                parseGameFromSnapshot(it)?.let { game ->
                    games.add(game)
                }
            }
            return games
        }

    }
}