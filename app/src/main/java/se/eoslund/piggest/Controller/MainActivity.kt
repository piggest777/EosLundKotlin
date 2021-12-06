package se.eoslund.piggest.Controller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.eoslund.piggest.R


class MainActivity : AppCompatActivity() {

    val gamesFragment = GamesFragment()
    val teamFragment = TeamFragment()
    val newsFragment = NewsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
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

    private fun replaceFragment(fragment: Fragment) {
        if(fragment != null)  {
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

}


