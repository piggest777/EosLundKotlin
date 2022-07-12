package se.eoslund.piggest.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import se.eoslund.piggest.R
import se.eoslund.piggest.databinding.ActivityYoutubePlayerBinding

class YoutubePlayerFullScreen : AppCompatActivity() {
    private lateinit var _binding: ActivityYoutubePlayerBinding
    private val binding get() = _binding
    private var videoID = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        _binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        videoID = intent.getStringExtra("videoID").toString()

        lifecycle.addObserver(binding.ytPlayer)
        binding.ytPlayer.apply {

            binding.ytPlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoID, 0f)
                }

                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    super.onError(youTubePlayer, error)
                    Toast.makeText(App.instance, error.name, Toast.LENGTH_SHORT).show()
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        finish()
                    }
                    super.onStateChange(youTubePlayer, state)
                }
            })

            enterFullScreen()

            addFullScreenListener(object : YouTubePlayerFullScreenListener {


                override fun onYouTubePlayerEnterFullScreen() {

                }

                override fun onYouTubePlayerExitFullScreen() {
                    finish()
                }
            })
        }


    }
}