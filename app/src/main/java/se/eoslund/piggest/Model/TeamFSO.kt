package se.eoslund.piggest.Model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.QuerySnapshot
import se.eoslund.piggest.Utilites.Constants.DEFAULT_TEAM_LOGO
import se.eoslund.piggest.Utilites.Constants.HOME_ARENA
import se.eoslund.piggest.Utilites.Constants.LOGO_PATH_NAME
import se.eoslund.piggest.Utilites.Constants.TEAM_CITY
import se.eoslund.piggest.Utilites.Constants.TEAM_NAME



class TeamFSO (val id: String, val name: String, val city: String, val homeArena: String, val logoPath: String) {


    companion object {

    fun parseTeamData(snapshot: QuerySnapshot): List<TeamFSO> {
        var teamArray = mutableListOf<TeamFSO>()

        snapshot.forEach {
            val data = it.data
            val id = it.id
            val name = data[TEAM_NAME] as? String ?: "Team"
            val city = data[TEAM_CITY] as? String ?: ""
            val homeArena = data[HOME_ARENA] as? String ?: ""
            val logoPath: String = data[LOGO_PATH_NAME] as? String ?: DEFAULT_TEAM_LOGO

            val newTeamFSO = TeamFSO(id, name, city, homeArena, logoPath)
            teamArray.add(newTeamFSO)
        }
        return teamArray
    }
    }
}