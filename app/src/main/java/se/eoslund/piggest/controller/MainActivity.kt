package se.eoslund.piggest.controller

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ListenerRegistration
import se.eoslund.piggest.R
import se.eoslund.piggest.model.Game
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import se.eoslund.piggest.utilites.Constants.BROADCAST_UPDATE_GAMES_TABLE


class MainActivity : AppCompatActivity() {

    val gamesFragment = GamesFragment()
    val teamFragment = TeamFragment()
    val newsFragment = NewsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (App.appReadyToUse) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        val intent = Intent(BROADCAST_UPDATE_GAMES_TABLE)
                        LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(gamesFragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)


       bottomNav.setOnItemSelectedListener {
           when (it.itemId){
               R.id.nav_games -> replaceFragment(gamesFragment)
               R.id.nav_team -> replaceFragment(teamFragment)
               R.id.nav_news -> replaceFragment((newsFragment))
           }
           true
       }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}


