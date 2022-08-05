package se.eoslund.piggest.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.GameAdapter
import se.eoslund.piggest.model.Game

class MatchListActivity : AppCompatActivity() {
    private lateinit var _binding: se.eoslund.piggest.databinding.ActivityMatchListBinding
     val binding get() = _binding
    lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        _binding = se.eoslund.piggest.databinding.ActivityMatchListBinding.inflate(layoutInflater)
        val view = binding.root

        super.onCreate(savedInstanceState)
        setContentView(view)

        binding.apply {
            matchListTitle.text = GamesFragment.chosenLeague.uppercase()
            matchListBackButton.setOnClickListener {
                finish()
            }
        }



        adapter = GameAdapter(GamesFragment.gameArray) { game ->
            Intent(App.instance, MatchActivity::class.java)
                .putExtra("game", game)
                .apply { startActivity(this) }
        }

        binding.matchListRecyclerView.adapter = adapter
        binding.matchListRecyclerView.layoutManager = LinearLayoutManager(this)


    }
}