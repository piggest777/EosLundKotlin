package se.eoslund.piggest.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.isNotEmpty
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.GameAdapter
import se.eoslund.piggest.controller.App.Companion.instance
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.model.TeamRO
import se.eoslund.piggest.services.DataService
import se.eoslund.piggest.services.DataService.toSnakeCase
import se.eoslund.piggest.services.DateFormat
import se.eoslund.piggest.services.DateFormatter
import se.eoslund.piggest.utilites.Constants.ALL_LEAGUES
import se.eoslund.piggest.utilites.Constants.BE_DAM_LEAGUE
import se.eoslund.piggest.utilites.Constants.BE_HERR_LEAGUE
import se.eoslund.piggest.utilites.Constants.BROADCAST_UPDATE_GAMES_TABLE
import se.eoslund.piggest.utilites.Constants.GAMES_REF
import se.eoslund.piggest.utilites.Constants.GAME_DATE_AND_TIME
import se.eoslund.piggest.utilites.Constants.SBLD_LEAGUE
import se.eoslund.piggest.utilites.Constants.SE_HERR_LEAGUE
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE
import se.eoslund.piggest.utilites.Constants.TOURNAMENT_TABLE
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GamesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    lateinit var radioGroup: RadioGroup

    lateinit var gameScheduleListener: ListenerRegistration
    lateinit var teamsUpdateReceiver: BroadcastReceiver
    lateinit var gamesList: RecyclerView
    lateinit var adapter: GameAdapter

    //next game UI element init
    lateinit var ngGamePlaceTextView: TextView
    lateinit var ngHomeLogoImageView: ImageView
    lateinit var ngAwayLogoImageView: ImageView
    lateinit var ngHomeTeamNameTextView: TextView
    lateinit var ngAwayTeamNameTextView: TextView
    lateinit var ngDateTextView: TextView
    lateinit var ngTimeTextView: TextView
    lateinit var ngTextView: TextView
    lateinit var ngTimeCounterTextView: TextView
    lateinit var comingSoonCard: CardView
    lateinit var ngCard: CardView
    lateinit var gamesLoadingAnim: LottieAnimationView
    lateinit var comingSoonText: TextView
    lateinit var comingSoonAnimationView: LottieAnimationView
    lateinit var fabOpenMatchTableButton: FloatingActionButton
    lateinit var ordinaryTournamentTableButton: MaterialButton
    lateinit var sbldButton: Button
    lateinit var sehButton: Button
    lateinit var allButton: Button
    lateinit var bedButton: Button
    lateinit var behButton: Button
    lateinit var linearLayoutLeagues: LinearLayout
    lateinit var horizontalScrollView: HorizontalScrollView

    var countDownTimer: CountDownTimer? = null


    private val db = Firebase.firestore
    private val fsSettings = firestoreSettings {
        isPersistenceEnabled = true
    }

    private val gamesReference = db.collection(GAMES_REF)

    var curGame: Game? = null
    var tournamentTableUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun updateGameView() {
        gameScheduleListener.remove()
        setListener()
        nextGameInfo()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        gamesList = view.findViewById(R.id.game_schedule_list)
        gamesList.itemAnimator = null
//        radioGroup = view.findViewById(R.id.radioGroup)
//        val sbldRadioButton: Button = view.findViewById(R.id.games_sbld_button)
//        sbldRadioButton.isChecked = true

        teamsUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("teamsUpdateReceiver", "onReceive")
                updateGameView()
            }
        }

        LocalBroadcastManager.getInstance(instance).registerReceiver(
            teamsUpdateReceiver,
            IntentFilter(BROADCAST_UPDATE_GAMES_TABLE)
        )


        //bing UI element
        ngGamePlaceTextView = view.findViewById(R.id.games_anons_place_tv)
        ngHomeLogoImageView = view.findViewById(R.id.games_anons_home_logo_iv)
        ngAwayLogoImageView = view.findViewById(R.id.game_anons_guest_logo_iv)
        ngHomeTeamNameTextView = view.findViewById(R.id.game_anons_home_team_name_tv)
        ngAwayTeamNameTextView = view.findViewById(R.id.game_anons_away_team_name_tv)
        ngTextView = view.findViewById(R.id.game_anons_next_game)
        ngDateTextView = view.findViewById(R.id.game_anons_date)
        ngTimeTextView = view.findViewById(R.id.game_anons_time)
        ngTimeCounterTextView = view.findViewById(R.id.game_anons_counter)
        comingSoonCard = view.findViewById(R.id.comming_soon_card)
        ngCard = view.findViewById(R.id.next_game_card_view)
        gamesLoadingAnim = view.findViewById(R.id.games_loading_animation)
        comingSoonAnimationView = view.findViewById(R.id.coming_soon_animation)
        comingSoonText = view.findViewById(R.id.coming_soon_text)
        fabOpenMatchTableButton = view.findViewById(R.id.fab_tournament_table)
        ordinaryTournamentTableButton = view.findViewById(R.id.tournament_btn)

        sbldButton = view.findViewById(R.id.games_sbld_button)
        sehButton = view.findViewById(R.id.games_seh_button)
        allButton = view.findViewById(R.id.games_all_button)
        bedButton = view.findViewById(R.id.games_bed_button)
        behButton = view.findViewById(R.id.be_herr_button)
        linearLayoutLeagues = view.findViewById(R.id.linear_layout_leagues)
        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)

        setupListenerForButtons()

        setUpCardSize()


        ngCard.setOnClickListener(View.OnClickListener {
            if (curGame != null) {
                val intent = Intent(activity, MatchActivity::class.java)
                intent.putExtra("game", curGame)
                startActivity(intent)
            }
        })

        ordinaryTournamentTableButton.setOnClickListener {
            getTournamentTableLink(chosenLeague)
        }

        fabOpenMatchTableButton.setOnClickListener {
            if (gamesList.isNotEmpty()) {
                Intent(activity, MatchListActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        adapter = GameAdapter(gameArray) { game ->
            Intent(instance, MatchActivity::class.java)
                .putExtra("game", game)
                .apply { startActivity(this) }
        }
        gamesList.adapter = adapter
        gamesList.layoutManager = LinearLayoutManager(instance)
        db.firestoreSettings = fsSettings
        if (gameArray.isEmpty()) {
            setupNextGameCard(true)
        }
        setListener()


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameScheduleListener.remove()
        LocalBroadcastManager.getInstance(instance).unregisterReceiver(teamsUpdateReceiver)
        countDownTimer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
    }


    override fun onResume() {
        super.onResume()
        countDownTimer?.start()
    }

    private fun setUpCardSize() {
        val height = resources.displayMetrics.heightPixels
        if (height < 1800) {
            instance.resources.getDimension(R.dimen.small_screen_dimen_for_card).toInt().let {
                ngCard.layoutParams.height = it
                ngTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.small_screen_next_game_text)
                )
                ngTimeTextView.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.small_screen_match_time_text)
                )
            }
        }
    }

    private fun setupListenerForButtons() {
        sbldButton.setOnClickListener {
            selectButton(sbldButton)
        }
        sehButton.setOnClickListener  {
            selectButton(sehButton)
        }
        allButton.setOnClickListener  {
            selectButton(allButton)
        }
        bedButton.setOnClickListener  {
            selectButton(bedButton) }
        behButton.setOnClickListener  {
            selectButton(behButton)
        }

        when (chosenLeague) {
            SBLD_LEAGUE -> selectButton(sbldButton)
            SE_HERR_LEAGUE -> selectButton(sehButton)
            ALL_LEAGUES -> selectButton(allButton)
            BE_DAM_LEAGUE -> selectButton(bedButton)
            BE_HERR_LEAGUE -> selectButton(behButton)
        }
    }

    private fun selectButton(selectedButton: Button) {
        for (i in 0 until linearLayoutLeagues.childCount) {
            val btn = linearLayoutLeagues.getChildAt(i) as? Button
            if (btn == selectedButton) {
                btn.isSelected = true
                centerButtonInScrollView(btn)
                val width = linearLayoutLeagues.getChildAt(i).width
                btn.invalidate()
            } else {
                btn?.isSelected = false
                btn?.invalidate()
            }
        }

                chosenLeague = when (selectedButton.text) {
                getString(R.string.sbld) -> SBLD_LEAGUE
                getString(R.string.se_herr) -> SE_HERR_LEAGUE
                getString(R.string.be_dam) -> BE_DAM_LEAGUE
                getString(R.string.all_teams) -> ALL_LEAGUES
                    getString(R.string.be_herr) -> BE_HERR_LEAGUE
                else -> SBLD_LEAGUE
            }
            countDownTimer?.cancel()
        if (::gameScheduleListener.isInitialized) {
            gameScheduleListener.remove()
        }
            setListener()
    }
    private fun centerButtonInScrollView(button: Button) {
        horizontalScrollView.post {
            val leftPos = button.left
            val scrollViewWidth = horizontalScrollView.width
            val scrollTo = leftPos - (scrollViewWidth / 2) + (button.width / 2) // add half the button width to perfectly center it

            horizontalScrollView.smoothScrollTo(scrollTo, 0)
        }
    }
    private fun setListener() {
        gameArray.clear()
        val leaguePredicate = if (chosenLeague == ALL_LEAGUES) {
            mutableListOf(SBLD_LEAGUE, SE_HERR_LEAGUE, BE_DAM_LEAGUE, BE_HERR_LEAGUE)
        } else {
            mutableListOf(chosenLeague)
        }
        gameScheduleListener = gamesReference
            .whereIn(TEAM_LEAGUE, leaguePredicate)
            .orderBy(GAME_DATE_AND_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("FB_ERROR", "listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    val game = Game.parseGameFromSnapshot(dc.document)
                    game?.let {
                        try {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> onDocumentAdded(dc, it)
                                DocumentChange.Type.MODIFIED -> onDocumentModified(dc, it)
                                DocumentChange.Type.REMOVED -> onDocumentRemoved(dc)
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            Log.e("FB_ERROR", "onEvent:error", e)
                            gameScheduleListener.remove()
                            setListener()
                        }
                    }
                }
                nextGameInfo()
            }
    }


    private fun onDocumentAdded(change: DocumentChange, game: Game) {
        gameArray.add(change.newIndex, game)
        adapter.notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange, game: Game) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            gameArray[change.oldIndex] = game
            adapter.notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            gameArray.removeAt(change.oldIndex)
            gameArray.add(change.newIndex, game)
            adapter.notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        gameArray.removeAt(change.oldIndex)
        adapter.notifyItemRemoved(change.oldIndex)
    }

    fun nextGameInfo() {
        gamesLoadingAnim.visibility = View.INVISIBLE
        gamesLoadingAnim.pauseAnimation()
        setupNextGameCard(false)
        curGame = getNextGame()

        if (curGame == null) {
            comingSoonCard.visibility = View.VISIBLE
            ngCard.visibility = View.INVISIBLE
            comingSoonAnimationView.playAnimation()
            gamesList.scrollToPosition(gameArray.size - 1)

        } else {
            comingSoonCard.visibility = View.INVISIBLE
            ngCard.visibility = View.VISIBLE

            scrollToPosition()

            countDownTimer?.cancel()
            setTimer(curGame!!.gameDateAndTime)

            ngDateTextView.text =
                DateFormatter.getFormattedDate(curGame!!.gameDateAndTime, DateFormat.DATE)
            ngTimeTextView.text =
                DateFormatter.getFormattedDate(curGame!!.gameDateAndTime, DateFormat.TIME)

            val rsTeam = TeamRO.getTeamById(curGame!!.rsTeamCode)
            val lsTeam = TeamRO.getTeamById(curGame!!.lsTeamCode)

            ngHomeLogoImageView.setImageDrawable(DataService.setUpTeamLogo(lsTeam.logoPathName.toSnakeCase()))
            ngAwayLogoImageView.setImageDrawable(DataService.setUpTeamLogo(rsTeam.logoPathName.toSnakeCase()))
            ngGamePlaceTextView.text = instance.getString(R.string.game_city, lsTeam.city)
            ngHomeTeamNameTextView.text = lsTeam.name
            ngAwayTeamNameTextView.text = rsTeam.name
        }
    }

    private fun scrollToPosition() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                curGame?.let {
                    val curGameIndex = gameArray.indexOf(it)
                    gamesList.smoothScrollToPosition(curGameIndex)
                }
            }
        }, 100)
    }

    private fun getNextGame(): Game? {
        val currentTime = Date()
        return gameArray.asSequence()
            .filter { it.gameDateAndTime > currentTime }
            .firstOrNull()
    }

    private fun setTimer(gameDate: Date) {
        val currentTime = Date().time
        val difference = gameDate.time - currentTime

        countDownTimer = object : CountDownTimer(difference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                if (elapsedDays > 2) {
                    ngTimeCounterTextView.text = "$elapsedDays days"
                } else if (elapsedDays > 1) {
                    ngTimeCounterTextView.text = "$elapsedDays day"
                } else {
                    val formattedTime = String.format(
                        "%02d:%02d:%02d",
                        elapsedHours,
                        elapsedMinutes,
                        elapsedSeconds
                    )
                    ngTimeCounterTextView.text = formattedTime
                }
            }

            override fun onFinish() {
                nextGameInfo()
            }
        }.start()

    }

    private fun setupNextGameCard(isLoading: Boolean) {
        if (isLoading) {
            gamesLoadingAnim.visibility = View.VISIBLE
            comingSoonCard.visibility = View.VISIBLE
            ngCard.visibility = View.INVISIBLE
            comingSoonText.text = "LOADING..."
            comingSoonAnimationView.setAnimation("basketball_net.json")
        } else {
            comingSoonCard.visibility = View.INVISIBLE
            ngCard.visibility = View.VISIBLE
            comingSoonText.text = instance.getString(R.string.coming_soon)
            comingSoonAnimationView.setAnimation("finger_basketball.json")
            comingSoonAnimationView.pauseAnimation()
            gamesLoadingAnim.visibility = View.INVISIBLE
            gamesLoadingAnim.pauseAnimation()
        }
    }

    fun getTournamentTableLink(league: String) {
        db.collection(TOURNAMENT_TABLE).document(league).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val link = document.getString("url")
                    if (link != null) {
                        tournamentTableUrl = link
                        requireActivity()
                            .supportFragmentManager
                            .beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                WebStatsFragment.newInstance("$tournamentTableUrl")
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GamesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic

        var chosenLeague = ALL_LEAGUES
        var gameArray = mutableListOf<Game>()

        fun newInstance(param1: String, param2: String) =
            GamesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}