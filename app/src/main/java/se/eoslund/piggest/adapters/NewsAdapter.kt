package se.eoslund.piggest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    inner class NewsHolder(binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val newsHeader: TextView = binding.newsTitle
        val newsDate: TextView = binding.newsDate
        val newsBody: TextView = binding.newsContent
        val newsImage: ImageView = binding.newsImageView
        val newsButton: AppCompatButton = binding.newsButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {


        return NewsHolder(
            NewsListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder:NewsHolder, position: Int) {
        val item = news[position]
        when (NewsFragment.segmentControlStatus) {
            NewsStatus.NEWS -> {
                holder.newsDate.visibility = View.VISIBLE
                holder.newsImage.visibility = View.VISIBLE
                holder.newsBody.visibility = View.VISIBLE
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
                holder.newsHeader.text = item.header
                holder.newsBody.visibility = View.GONE
                holder.newsImage.setOnClickListener{
                    itemClick(item)
                }
                holder.newsButton.visibility = View.GONE
            }
        }


    }


}
