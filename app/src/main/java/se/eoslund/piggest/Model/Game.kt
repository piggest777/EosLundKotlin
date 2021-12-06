package se.eoslund.piggest.Model

import java.util.*

class Game(val eosTeamCode: String,
           val oppositeTeamCode: String,
           val isHomeGame: Boolean,
           val eosScore: Int,
           val oppositeTeamScore: Int,
           val gameDateAndTime: Date,
           val teamLeague: String,
           val documentId: String,
           val eosPlayers: String?,
           val oppTeamPlayer: String?,
           val statsLink: String?,
           val gameDescription: String?,
           val gameCoverURL: String?
) {
}