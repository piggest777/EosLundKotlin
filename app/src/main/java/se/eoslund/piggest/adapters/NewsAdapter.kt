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
import se.eoslund.piggest.databinding.FragmentNewsBinding
import se.eoslund.piggest.databinding.FragmentPlayerBinding
import se.eoslund.piggest.databinding.NewsListItemBinding
import se.eoslund.piggest.model.News

class NewsAdapter(private val news: List<News>, private val itemClick: (News) ->Unit) : RecyclerView.Adapter<NewsHolder>() {

    inner class NewsHolder(binding: NewsListItemBinding, val itemClick: (News) -> Unit) : RecyclerView.ViewHolder(binding.root) {
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
            ), itemClick
        )
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder:NewsHolder, position: Int) {
        val item = news[position]

        holder.newsHeader.text = item.header
        holder.newsDate.text = item.date
        holder.newsBody.text = item.content
        Glide.with(holder.newsImage.context).load(item.imageLink).placeholder(R.drawable.default_news_image).into(holder.newsImage)
        holder.newsButton.setOnClickListener { itemClick(item) }
    }


}
