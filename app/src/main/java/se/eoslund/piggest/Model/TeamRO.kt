package se.eoslund.piggest.Model

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import se.eoslund.piggest.Controller.App

open class TeamRO(
    @PrimaryKey
    var id: String = "NULL",
    @Required
    var name: String = "",
    var teamCity: String = "",
    var homeArena: String = "",
    var logoPathName: String = "defaultTeamLogo.png"
) : RealmObject() {

    companion object{


        suspend fun addTeamWithCallback(fireTeam: TeamFSO, completion:(Boolean)->Unit) {
            val realm: Realm = Realm.getDefaultInstance()
            val newTeamRO =
                TeamRO(fireTeam.id, fireTeam.name, fireTeam.city, fireTeam.homeArena, fireTeam.logoPath)

            realm.executeTransactionAsync({ transactionRealm ->
                transactionRealm.insertOrUpdate(newTeamRO)
            }, {
                completion(true)
                Log.v("Realm", "Successfully completed the transaction")
            }, { error ->
                completion(false)
                Log.e("Realm", "Failed the transaction: $error")
            })
        }

        fun addAllTeamsWithCallback(teamList: List<TeamFSO>, completion: (Boolean) -> Unit) {
            val realm: Realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({ transactionRealm ->
                teamList.forEach {
                    val newTeamRO =
                        TeamRO(it.id, it.name, it.city, it.homeArena, it.logoPath)
                    transactionRealm.insertOrUpdate(newTeamRO)
                }
            }, {
                completion(true)
                Log.v("Realm", "Successfully completed the transaction")
            }, { error ->
                completion(false)
                Log.e("Realm", "Failed the transaction: $error")
            })
        }

        fun getAllTeamsFromRealm(): RealmQuery<TeamRO>? {
            val realm: Realm = Realm.getDefaultInstance()
            return realm.where(TeamRO::class.java)
        }
    }



}