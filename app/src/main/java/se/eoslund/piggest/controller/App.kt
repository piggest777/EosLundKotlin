package se.eoslund.piggest.controller

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.os.Build
import android.provider.Settings.System.getString
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.realm.Realm
import io.realm.RealmConfiguration
import se.eoslund.piggest.R
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.model.TeamFSO
import se.eoslund.piggest.model.TeamRO
import se.eoslund.piggest.services.Prefs
import se.eoslund.piggest.utilites.Constants.GAMES_REF
import se.eoslund.piggest.utilites.Constants.TEAMS_REF
import se.eoslund.piggest.utilites.Constants.TEAM_INFO_UPDATE_DATE
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
        val realmName = "EosRealmBase"
        val realmConfig = RealmConfiguration.Builder().name(realmName).build()
        Realm.setDefaultConfiguration(realmConfig)


        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

         val db = Firebase.firestore
         val teamRef = db.collection(TEAMS_REF)
        val gamesRef = db.collection(GAMES_REF)
         val fsSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = fsSettings

        createNotificationChannel()

        updateTeamChecker(teamRef){ success ->
            if(success) {
                Log.d("UPDATE", "successfully updated team base")
                appReadyToUse = true
            }
        }

        getCachedGames(GamesFragment.chosenLeague, gamesRef)
        getRegistrationToken()
        if (!checkGooglePlayServices()) {
            Log.w("GOOGLE_PLAY", "Device doesn't have google play services")
        }
    }

    companion object {
        lateinit var instance: App
        var prefs: Prefs? = null
        var appReadyToUse = false

        fun getCachedGames(league: String, ref: CollectionReference) {
            ref.whereEqualTo("league", league)
                .get(Source.CACHE)
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        for (document in it.result!!.documents) {
                            val game = Game.parseGameFromSnapshot(document)
                            if (game != null) {
                                GamesFragment.gameArray.add(game)
                            }
                        }
                    }
                }
        }

        private fun loadTeamInfoFromFirebase(
            ref: CollectionReference,
            returnedTeamArray: (List<TeamFSO>) -> Unit
        ) {
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
                                                    updatePrefsToCurrentDate()
                                                    completionHandler(true)
                                                }
                                             }
                                } else{
                                    Log.d("UPDATE", "no update needed")
                                    appReadyToUse = true
                                }
                            }

                        } else {
                            Log.d("FIREBASE", "No such document")
                            appReadyToUse = true
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("FIREBASE", "get failed with ", exception)
                        appReadyToUse  = true
                    }
            } else {
                Log.d("UPDATE", "Updating team base")
                updateTeamRealmBase { success ->
                    if(success) {
                        updatePrefsToCurrentDate()
                        completionHandler(true)
                    }
                }
            }
        }

        private fun updateTeamRealmBase(updaterStatus: (Boolean) -> Unit) {
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

        private fun updatePrefsToCurrentDate(){
            val currentDate = Date()
            prefs!!.teamUpdateDate = currentDate.time
            Log.d("PREFS", "current date is ${prefs!!.teamUpdateDate}")
        }

        private fun getRegistrationToken() {
             FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TOKEN", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = instance.getString(R.string.msg_token_fmt, token)
                Log.d("TOKEN", msg)
            })
        }

        private fun checkGooglePlayServices(): Boolean {
            // 1
            val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(instance)
            // 2
            return if (status != ConnectionResult.SUCCESS) {
                Log.e(TAG, "Error")
                // ask user to update google play services and manage the error.
                false
            } else {
                // 3
                Log.i("GOOGLE_PLAY", "Google play services updated")
                true
            }
        }

        private fun createNotificationChannel() {
            // Create the NotificationChannel
            val name = instance.getString(R.string.default_channel_name)
            val descriptionText = instance.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(instance.getString(R.string.default_notification_channel_id), name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = instance.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}