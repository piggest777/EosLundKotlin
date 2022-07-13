package se.eoslund.piggest.controller

import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
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

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val orientationEventListener = object : OrientationEventListener(this@YoutubePlayerFullScreen, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation in 60..120 || orientation in 240..300) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                    this.disable()
                }
            }
        }

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        }

        binding.ytPlayer.apply {
            val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()

            binding.ytPlayer.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {

                    val defaultPlayerController = DefaultPlayerUiController(binding.ytPlayer, youTubePlayer)
                    binding.ytPlayer.setCustomPlayerUi(defaultPlayerController.rootView)

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
            }, options)


            addFullScreenListener(object : YouTubePlayerFullScreenListener {


                override fun onYouTubePlayerEnterFullScreen() {

                }

                override fun onYouTubePlayerExitFullScreen() {

                }
            })
        }
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            hideSystemUI()
//        }
//    }
//
//    private fun hideSystemUI() {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
//            controller.hide(WindowInsetsCompat.Type.systemBars())
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//    }


}