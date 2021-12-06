package se.eoslund.piggest.Controller

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
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

        Realm.init(this)

        val db = Firebase.firestore
        val docRef = db.collection("teams")

        loadTeamInfoFromFirebase(docRef)

        val realmName: String = "EosRealmBase"
        realmConfig = RealmConfiguration.Builder().name(realmName).build()
        val backgroundThreadRealm : Realm = Realm.getInstance(realmConfig)
    }

    companion object {
        lateinit var instance: App
        lateinit var realmConfig: RealmConfiguration
        var prefs: Prefs? = null



        fun loadTeamInfoFromFirebase(ref: CollectionReference){
            ref.get().addOnSuccessListener { snapshot ->
                val teamArray = TeamFSO.parseTeamData(snapshot)
            }
        }

        fun updateTeamChecker(teamRef: CollectionReference, completionHandler: (Boolean)-> Void){
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
                                    
                                }
                            }

                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }
    }
}