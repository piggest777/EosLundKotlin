package se.eoslund.piggest.adapters

import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.NewsAdapter.NewsHolder
import se.eoslund.piggest.controller.App
import se.eoslund.piggest.controller.NewsFragment
import se.eoslund.piggest.controller.NewsStatus
import se.eoslund.piggest.databinding.NewsListItemBinding
import se.eoslund.piggest.model.News

class NewsAdapter(
    private val news: List<News>,
    private val itemClick: (News) ->Unit) : RecyclerView.Adapter<NewsHolder>() {

    inner class NewsHolder(binding: NewsListItemBinding,  val itemClick: (News) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        val newsHeader: TextView = binding.newsTitle
        val newsDate: TextView = binding.newsDate
        val newsBody: TextView = binding.newsContent
        val newsImage: ImageView = binding.newsImageView
        val newsButton: AppCompatButton = binding.newsButton
        val ytPlayerView: YouTubePlayerView = binding.ytPlayer
        val ytPlayButton: AppCompatImageButton= binding.ytPlayButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {


        return NewsHolder(
            NewsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), itemClick
        )
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder:NewsHolder, position: Int) {
        val item = news[position]

        when (NewsFragment.segmentControlStatus) {
            NewsStatus.NEWS -> {
                holder.newsDate.visibility = View.VISIBLE
                holder.newsImage.visibility = View.VISIBLE
                holder.ytPlayerView.visibility = View.INVISIBLE
                holder.newsHeader.visibility = View.VISIBLE
                holder.ytPlayButton.visibility = View.INVISIBLE
                holder.newsHeader.text = item.header
                holder.newsDate.text = item.date
                holder.newsBody.text = item.content
                Glide.with(holder.newsImage.context).load(item.imageLink).placeholder(R.drawable.default_news_image).into(holder.newsImage)
                holder.newsButton.setOnClickListener { itemClick(item) }
                holder.newsButton.visibility = View.VISIBLE
                holder.newsButton.text = App.instance.getString(R.string.read_more)
            }

            NewsStatus.VIDEO -> {
                Glide.with(holder.newsImage.context).load(item.imageLink).into(holder.newsImage)
                holder.newsDate.visibility = View.INVISIBLE
                holder.newsHeader.visibility = View.INVISIBLE
                holder.newsBody.text = item.header
                holder.ytPlayButton.visibility = View.INVISIBLE
                    holder.ytPlayerView.addYouTubePlayerListener(
                        object : AbstractYouTubePlayerListener(){
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                holder.ytPlayButton.visibility = View.VISIBLE
                                holder.ytPlayButton.setOnClickListener {
                                    holder.newsImage.visibility = View.INVISIBLE
                                    holder.ytPlayerView.visibility = View.VISIBLE
                                    holder.ytPlayButton.visibility = View.INVISIBLE
                                    youTubePlayer.loadVideo(item.link, 0f)
                                }

                            }

                            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                                super.onError(youTubePlayer, error)
                                Toast.makeText(App.instance, error.name, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                holder.ytPlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
                    override fun onYouTubePlayerEnterFullScreen() {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                        holder.ytPlayerView.enterFullScreen()
                    }

                    override fun onYouTubePlayerExitFullScreen() {
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        holder.ytPlayerView.exitFullScreen()
                    }

                })
                holder.newsButton.setOnClickListener { itemClick(item) }
                holder.newsButton.visibility = View.GONE
            }
        }


    }


}
