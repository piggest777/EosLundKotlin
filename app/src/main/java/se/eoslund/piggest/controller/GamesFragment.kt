package se.eoslund.piggest.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings.Global.getString
import android.provider.Settings.System.getString
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.GameAdapter
import se.eoslund.piggest.controller.App.Companion.instance
import se.eoslund.piggest.model.Game
import se.eoslund.piggest.model.TeamRO
import se.eoslund.piggest.services.DataService.getResource
import se.eoslund.piggest.services.DateFormat
import se.eoslund.piggest.services.DateFormatter
import se.eoslund.piggest.utilites.Constants.ALL_LEAGUE
import se.eoslund.piggest.utilites.Constants.BE_DAM_LEAGUE
import se.eoslund.piggest.utilites.Constants.EOS_TEAM
import se.eoslund.piggest.utilites.Constants.GAMES_REF
import se.eoslund.piggest.utilites.Constants.GAME_DATE_AND_TIME
import se.eoslund.piggest.utilites.Constants.SBLD_LEAGUE
import se.eoslund.piggest.utilites.Constants.SE_HERR_LEAGUE
import se.eoslund.piggest.utilites.Constants.TEAM_LEAGUE
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
    lateinit var radioGroup: RadioGroup

    lateinit var gameScheduleListener: ListenerRegistration
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
    lateinit var ngTimeCounterTextView: TextView
    lateinit var comingSoonCard: CardView
    lateinit var ngCard: CardView
    lateinit var gamesLoadingAnim: LottieAnimationView
    lateinit var comingSoonText: TextView
    lateinit var comingSoonAnimationView: LottieAnimationView
    var countDownTimer: CountDownTimer? = null



    var gameArray  = mutableListOf<Game>()
    private var chosenLeague = "SBLD"


    private val db = Firebase.firestore
    private val gamesReference  = db.collection(GAMES_REF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        gamesList = view.findViewById(R.id.game_schedule_list)
        gamesList.itemAnimator = null
        radioGroup = view.findViewById(R.id.radioGroup)
        val sbldRadioButton: RadioButton = view.findViewById(R.id.news_radio_button)
        sbldRadioButton.isChecked = true

        //bing UI element
        ngGamePlaceTextView = view.findViewById(R.id.games_anons_place_tv)
        ngHomeLogoImageView = view.findViewById(R.id.games_anons_home_logo_iv)
        ngAwayLogoImageView = view.findViewById(R.id.game_anons_guest_logo_iv)
        ngHomeTeamNameTextView = view.findViewById(R.id.game_anons_home_team_name_tv)
        ngAwayTeamNameTextView = view.findViewById(R.id.game_anons_away_team_name_tv)
        ngDateTextView = view.findViewById(R.id.game_anons_date)
        ngTimeTextView = view.findViewById(R.id.game_anons_time)
        ngTimeCounterTextView = view.findViewById(R.id.game_anons_counter)
        comingSoonCard = view.findViewById(R.id.comming_soon_card)
        ngCard = view.findViewById(R.id.next_game_card_view)
        gamesLoadingAnim = view.findViewById(R.id.games_loading_animation)
        comingSoonAnimationView = view.findViewById(R.id.coming_soon_animation)
        comingSoonText = view.findViewById(R.id.coming_soon_text)


        radioGroup.setOnCheckedChangeListener { radioGroup, radioButtonID ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(radioButtonID)
            chosenLeague = when (selectedRadioButton.text) {
                getString(R.string.sbld) -> SBLD_LEAGUE
                getString(R.string.se_herr) -> SE_HERR_LEAGUE
                getString(R.string.be_dam) -> BE_DAM_LEAGUE
                getString(R.string.all_teams) -> ALL_LEAGUE
                else -> SBLD_LEAGUE
            }
            countDownTimer?.cancel()
            gameScheduleListener.remove()
            setListener()
        }

        adapter = GameAdapter(gameArray) { game ->
            Intent(instance, MatchActivity::class.java)
                .putExtra("game", game)
                .apply { startActivity(this) }
        }
        gamesList.adapter = adapter
        gamesList.layoutManager = LinearLayoutManager(instance)

        gameArray.clear()
        gamesLoadingAnim.visibility = View.VISIBLE
        setupNextGameCard(true)
        setListener()
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameScheduleListener.remove()
        countDownTimer?.cancel()
    }

    override fun getView(): View? {
        return super.getView()
    }

    private fun setListener(){
        val leaguePredicate = if (chosenLeague == ALL_LEAGUE) {
            mutableListOf(SBLD_LEAGUE, SE_HERR_LEAGUE, BE_DAM_LEAGUE)
        } else {
            mutableListOf(chosenLeague)
        }
        gameScheduleListener = gamesReference
            .whereIn(TEAM_LEAGUE, leaguePredicate)
            .orderBy(GAME_DATE_AND_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener{ snapshots, e ->
            if (e != null) {
                Log.w("FB_ERROR", "listen:error", e)
                return@addSnapshotListener
            }
                val currentSize = gameArray.size
                gameArray.clear()

                adapter.notifyItemRangeRemoved(0, currentSize)
                adapter.notifyItemRangeInserted(0, snapshots!!.size())
                for (dc in snapshots.documentChanges) {
                    val game = Game.parseGameFromSnapshot(dc.document)
                    game?.let {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> onDocumentAdded(dc, it)
                            DocumentChange.Type.MODIFIED -> onDocumentModified(dc, it)
                            DocumentChange.Type.REMOVED -> onDocumentRemoved(dc)
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
        val nextGame = getNextGame()

        if (nextGame == null) {
            comingSoonCard.visibility = View.VISIBLE
            ngCard.visibility = View.INVISIBLE
            comingSoonAnimationView.playAnimation()

        } else {
            comingSoonCard.visibility = View.INVISIBLE
            ngCard.visibility = View.VISIBLE

            val oppTeam: TeamRO

            val defaultTeamLogo = AppCompatResources.getDrawable(instance, R.drawable.default_image_logo)

            setTimer(nextGame.gameDateAndTime)

            ngDateTextView.text = DateFormatter.getFormattedDate(nextGame.gameDateAndTime, DateFormat.DATE)
            ngTimeTextView.text = DateFormatter.getFormattedDate(nextGame.gameDateAndTime, DateFormat.TIME)

            if (nextGame.isHomeGame) {
                oppTeam = TeamRO.getTeamById(nextGame.rsTeamCode)
                val oppTeamLogo = instance.getResource(oppTeam.logoPathName)
                ngGamePlaceTextView.text = instance.getString(R.string.game_place, EOS_TEAM.city, EOS_TEAM.homeArena)
                ngAwayLogoImageView.setImageDrawable(oppTeamLogo ?: defaultTeamLogo!!)
                ngHomeLogoImageView.setImageResource(R.drawable.eos_logo)
                ngHomeTeamNameTextView.text = EOS_TEAM.name
                ngAwayTeamNameTextView.text = oppTeam.name
            } else {
                oppTeam = TeamRO.getTeamById(nextGame.lsTeamCode)
                val oppTeamLogo = instance.getResource(oppTeam.logoPathName)
                ngGamePlaceTextView.text = instance.getString(R.string.game_place, oppTeam.city, oppTeam.homeArena)
                ngHomeLogoImageView.setImageDrawable(oppTeamLogo ?: defaultTeamLogo!!)
                ngAwayLogoImageView.setImageResource(R.drawable.eos_logo)
                ngAwayTeamNameTextView.text = EOS_TEAM.name
                ngHomeTeamNameTextView.text = oppTeam.name
            }
        }
    }

    private fun getNextGame(): Game? {
        val currentTime = Date()
        return gameArray.asSequence()
            .filter { it.gameDateAndTime > currentTime }
            .firstOrNull()
    }

    private fun setTimer (gameDate: Date) {
        val currentTime = Date().time
        val difference   =  gameDate.time - currentTime

        countDownTimer  = object : CountDownTimer(difference, 1000){
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

                if(elapsedDays > 1) {
                    ngTimeCounterTextView.text = "$elapsedDays days"
                } else {
                    ngTimeCounterTextView.text = "$elapsedHours : $elapsedMinutes : $elapsedSeconds"
                }
            }

            override fun onFinish() {
                nextGameInfo()
            }
        }.start()

    }

    private fun setupNextGameCard(isLoading: Boolean) {
        if (isLoading) {
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
        fun newInstance(param1: String, param2: String) =
            GamesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}