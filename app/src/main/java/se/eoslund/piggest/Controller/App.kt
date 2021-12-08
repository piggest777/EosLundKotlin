package se.eoslund.piggest.Controller

import android.app.Application
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.realm.Realm
import io.realm.RealmConfiguration
import se.eoslund.piggest.Model.TeamFSO
import se.eoslund.piggest.Model.TeamRO
import se.eoslund.piggest.Services.Prefs
import se.eoslund.piggest.Utilites.Constants.TEAM_INFO_UPDATE_DATE
import java.util.*

val prefs: Prefs by lazy {
    App.prefs!!
}

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(instance)

        Realm.init(instance)
        val realmName: String = "EosRealmBase"
        var realmConfig = RealmConfiguration.Builder().name(realmName).build()
        val backgroundThreadRealm : Realm = Realm.getInstance(realmConfig)
        Realm.setDefaultConfiguration(realmConfig)


        val db = Firebase.firestore
        val docRef = db.collection("teams")
         var date = prefs!!.teamUpdateDate
        Log.d("PREFS", "current date is $date")

        updateTeamChecker(docRef){ success ->
            if(success) {
                Log.d("UPDATE", "successfully updated team base")
            }
        }


    }

    companion object {
        lateinit var instance: App
//        lateinit var realmConfig: RealmConfiguration
        var prefs: Prefs? = null

//        val realmName: String = "EosRealmBase"
//        var realmConfig = RealmConfiguration.Builder().name(realmName).build()
//        val backgroundThreadRealm : Realm = Realm.getInstance(realmConfig)




        fun loadTeamInfoFromFirebase(ref: CollectionReference, returnedTeamArray: (List<TeamFSO>) -> Unit){
            ref.get().addOnSuccessListener { snapshot ->
                val teamArray = TeamFSO.parseTeamData(snapshot)
                returnedTeamArray(teamArray)
            }
                .addOnFailureListener { exception ->
                    Log.d("FIREBASE", "team load get failed with ", exception)
                }
        }

        fun updateTeamChecker(teamRef: CollectionReference, completionHandler: (Boolean)-> Unit){
            val nullLong: Long = -1
            val teams = TeamRO.getAllTeamsFromRealm()
            val teamsCount = teams?.count()
            if (prefs!!.teamUpdateDate != nullLong && teams != null && teamsCount!! > 0){
                teamRef.document(TEAM_INFO_UPDATE_DATE).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val data = document.data
                            val timestampDate = data?.get("updateDate") as? Timestamp
                            timestampDate?.let {
                                val fireBaseDateValue = it.toDate()
                                val appUpdateDate = Date(prefs!!.teamUpdateDate)
                                if (fireBaseDateValue > appUpdateDate){
                                            updateTeamRealmBase { success ->
                                                if(success) {
                                                    completionHandler(true)
                                                }
                                             }
                                }
                            }

                        } else {
                            Log.d("FIREBASE", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("FIREBASE", "get failed with ", exception)
                    }
            } else {
                Log.d("UPDATE", "Updating team base" )
                updateTeamRealmBase { success ->
                    if(success) {
                        completionHandler(true)
                    }
                }
            }
        }

        fun updateTeamRealmBase(updaterStatus: (Boolean)-> Unit){
            val db = Firebase.firestore
            val teamRef = db.collection("teams")
            loadTeamInfoFromFirebase(teamRef) { teams ->
                TeamRO.addAllTeamsWithCallback(teams) { success ->
                    if (success) {
                        updaterStatus(true)
                    }
                }
            }
        }


    }
}