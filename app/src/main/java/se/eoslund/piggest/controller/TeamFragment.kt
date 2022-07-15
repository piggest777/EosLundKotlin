package se.eoslund.piggest.controller

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.skydoves.progressview.ProgressView
import com.skydoves.progressview.ProgressViewAnimation
import kotlinx.coroutines.*
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.TeamAdapter
import se.eoslund.piggest.model.Player
import se.eoslund.piggest.model.PlayerRO
import se.eoslund.piggest.services.CallbackAggregator
import se.eoslund.piggest.utilites.Constants
import se.eoslund.piggest.utilites.Constants.BASE_UPDATE_DATE
import se.eoslund.piggest.utilites.Constants.DATE
import se.eoslund.piggest.utilites.Constants.PLAYERS_REF
import se.eoslund.piggest.utilites.Constants.PLAYER_BASE_UPDATE_DATE
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

enum class BaseStatus {
    NOT_EXIST,
    NEED_TO_BE_UPDATED,
    READY_TO_USE,
    ERROR
}

class TeamFragment : Fragment(), CoroutineScope by MainScope() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var playerList: RecyclerView
    private lateinit var adapter: TeamAdapter
    private lateinit var progressView: ProgressView
    private lateinit var loadingAnim: LottieAnimationView
    private var playerArray = mutableListOf<PlayerRO>()
    private var fsPlayerArray = mutableListOf<Player>()
    private lateinit var segmentControl: RadioGroup
    private var chosenLeague = "SBLD"

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team, container, false)
        playerList = view.findViewById(R.id.team_recycle_view)
        segmentControl = view.findViewById(R.id.radio_group_news)
        progressView = view.findViewById(R.id.update_team_progress_view)
        loadingAnim = view.findViewById(R.id.team_loading_animation)
        progressView.progress = 0f
        progressView.visibility = View.GONE
        val sbldRadioButton = view.findViewById<RadioButton>(R.id.news_radio_button)
        sbldRadioButton.isChecked = true

        segmentControl.setOnCheckedChangeListener { radioGroup, radioButtonID ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(radioButtonID)
            chosenLeague = when (selectedRadioButton.text) {
                getString(R.string.sbld) -> Constants.SBLD_LEAGUE
                getString(R.string.se_herr) -> Constants.SE_HERR_LEAGUE
                getString(R.string.be_dam) -> Constants.BE_DAM_LEAGUE
                getString(R.string.all_teams) -> Constants.ALL_LEAGUE
                else -> Constants.SBLD_LEAGUE
            }
            playerArray.clear()
            playerArray.addAll(PlayerRO.getPlayersFromLeague(chosenLeague))
            adapter.notifyDataSetChanged()
        }

        playerArray.addAll(PlayerRO.getPlayersFromLeague(chosenLeague))

        adapter = TeamAdapter(playerArray) {
            Intent(App.instance, PlayerActivity::class.java)
                .putExtra("playerID", it.id)
                .apply { startActivity(this) }

        }

        playerList.adapter = adapter
        playerList.layoutManager = GridLayoutManager(App.instance, 2)


        trackPlayerBaseUpdate { baseStatus ->
            when (baseStatus) {
                BaseStatus.READY_TO_USE -> {
                    Log.d("PLAYERS", "BASE SUCCESSFULLY UPDATED AND READY TO USE")
                }
                BaseStatus.NOT_EXIST -> {
                    getPlayerListFromFS { success, fsPlayerList ->
                        if (success) {

                                updateRealmBase(fsPlayerList, null)

                        }
                    }
                }
                BaseStatus.NEED_TO_BE_UPDATED -> {
                    getPlayerListFromFS { success, fsPlayerList ->
                        if (success) {
                            var listToUpdate = mutableListOf<String>()
                            var listToDelete = mutableListOf<String>()

                            val realmPlayers = PlayerRO.getAllPlayerFromBase()

                            val fsIDs = firPlayersIdsAndDate(fsPlayerList)
                            var realmIds = realmPlayersIDsAndDate(realmPlayers)

                            fsIDs.forEach { (firId, firUpdateDate) ->
                                if (realmIds[firId] != null) {
                                    if (firUpdateDate > realmIds[firId]) {
                                        listToUpdate.add(firId)
                                        realmIds.remove(firId)
                                    } else {
                                        realmIds.remove(firId)
                                    }
                                } else {
                                    listToUpdate.add(firId)
                                }
                            }
                            listToDelete.addAll(ArrayList(realmIds.keys))

                            if (listToUpdate.isNotEmpty() && listToDelete.isNotEmpty()) {
                                val playersToUpdate = fsPlayerList.filter {
                                    listToUpdate.contains(it.id)
                                }

                                lifecycleScope.launch() {
                                    updateRealmBase(playersToUpdate, listToDelete)
                                }
                            } else {
                                Log.d("TEAM", "No player to update or delete")
                            }

                        }
                    }

                }
                BaseStatus.ERROR -> Log.d("TEAM", "Error while fetch players")
            }
        }

        return view
    }

    private fun trackPlayerBaseUpdate(completionHandler: (BaseStatus) -> Unit) {
        val realmPlayerArray = PlayerRO.getAllPlayerFromBase()
        val emptyDate: Long = -1
        if (realmPlayerArray.isEmpty() || prefs.playersUpdateDate == emptyDate) {
            completionHandler(BaseStatus.NOT_EXIST)
        } else {
            db.collection(BASE_UPDATE_DATE).document(PLAYER_BASE_UPDATE_DATE).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.data != null) {
                        val data = document.data
                        val updateDate = data!![DATE] as? Timestamp
                        if (updateDate != null) {
                            val timeStampToDate = updateDate.toDate()
                            val prefsDate = Date(prefs.playersUpdateDate)
                            if (timeStampToDate > prefsDate) {
                                completionHandler(BaseStatus.NEED_TO_BE_UPDATED)
                            } else (completionHandler(BaseStatus.READY_TO_USE))
                        } else completionHandler(BaseStatus.ERROR)

                    } else {
                        Log.d("FIREBASE", "No such document: Player base update date")
                        completionHandler(BaseStatus.ERROR)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("FIREBASE", "get failed with ", exception)
                    completionHandler(BaseStatus.ERROR)
                }
        }
    }


    private fun getPlayerListFromFS(completionHandler: (Boolean, MutableList<Player>) -> Unit) {
        db.collection(PLAYERS_REF)
            .get()
            .addOnSuccessListener { result ->
                val playerList = Player.parsePlayers(result)
                completionHandler(true, playerList)
            }
            .addOnFailureListener { exception ->
                completionHandler(false, mutableListOf())
                Log.d("FIREBASE", "Cannot fetch players from FB: ", exception)
            }
    }

    private  fun updateRealmBase(players: List<Player>, playersToDelete: List<String>?) {
        val playersRealm = mutableListOf<PlayerRO>()
        val count = players.size
        val aggregator = CallbackAggregator(count)
        progressView.visibility = View.VISIBLE
        progressView.autoAnimate = false
        progressView.duration = 2000L
        progressView.progressAnimation = ProgressViewAnimation.NORMAL
        progressView.progressFromPrevious = false
        progressView.min = 5f

        loadingAnim.visibility = View.VISIBLE

        players.forEach { player ->
            if (player.imageUrl == null) {
                playersRealm.add(mapPlayerFSOtoRO(player, null))
                aggregator.increment()
                incrementProgress(aggregator.calls, count)
            } else {
                Player.getPlayerImage(player.imageUrl!!) { success, byteArray ->
                    if (success) {
                        playersRealm.add(mapPlayerFSOtoRO(player, byteArray))
                        aggregator.increment()
                        incrementProgress(aggregator.calls, count)
                    } else {
                        playersRealm.add(mapPlayerFSOtoRO(player, null))
                        aggregator.increment()
                        incrementProgress(aggregator.calls, count)
                    }
                }
            }
        }
            aggregator.finalCallback = {
                deletePlayersFromBase(playersToDelete)
                PlayerRO.addAllPlayersWithCallback(playersRealm) { success ->
                    if (success) {
                        progressView.visibility = View.GONE
                        loadingAnim.visibility = View.GONE
                        prefs.playersUpdateDate = Date().time
                        playerArray.clear()
                        playerArray.addAll(PlayerRO.getPlayersFromLeague(chosenLeague))

                        adapter.notifyDataSetChanged()
                    }
                }
            }

//        lifecycleScope.launch {
//            val job = players.map { player ->
//                async(Dispatchers.IO) {
//
//                    if (player.imageUrl == null) {
//                        playersRealm.add(mapPlayerFSOtoRO(player, null))
//                    } else {
//                        PlayerFSO.getPlayerImage(player.imageUrl!!) { success, byteArray ->
//                            if (success) {
//                                playersRealm.add(mapPlayerFSOtoRO(player, byteArray))
//                            } else {
//                                playersRealm.add(mapPlayerFSOtoRO(player, null))
//                            }
//                        }
//                    }
//                }
//            }.awaitAll()
//            whenStarted {
//                job
//            }

    }

    private fun firPlayersIdsAndDate(fsPlayers: List<Player>): HashMap<String, Date> {
        val iDs = hashMapOf<String, Date>()
        fsPlayers.forEach {
            iDs[it.id] = it.updateDate
        }
        return iDs
    }

    private fun realmPlayersIDsAndDate(realmPlayers: List<PlayerRO>): HashMap<String, Date> {
        val iDs = hashMapOf<String, Date>()
        realmPlayers.forEach {
            iDs[it.id] = it.updateDate
        }
        return iDs
    }

    private fun deletePlayersFromBase(iDsToDelete: List<String>?) {
        iDsToDelete?.forEach {
            PlayerRO.deletePlayerById(it)
        }
    }

    private fun mapPlayerFSOtoRO(player: Player, byteArray: ByteArray?): PlayerRO {
        return PlayerRO(
            player.id,
            byteArray,
            player.bigImageURL,
            player.updateDate,
            player.height,
            player.dateOfBirth,
            player.nationality,
            player.originalClub,
            player.inEosFrom,
            player.name,
            player.position,
            player.league,
            player.number.toInt()
        )
    }

    fun incrementProgress(calls: Int, count:Int) {
        val progressPercent:  Int = ((calls.toFloat()/count.toFloat()) * 100).toInt()
        progressView.progress = progressPercent.toFloat()
        progressView.labelText = App.instance.getString(R.string.progress_view_update_percent, "$progressPercent %")
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