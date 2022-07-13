package se.eoslund.piggest.controller

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.android.tools.build.jetifier.core.utils.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import se.eoslund.piggest.R
import se.eoslund.piggest.adapters.NewsAdapter
import se.eoslund.piggest.databinding.FragmentNewsBinding
import se.eoslund.piggest.model.News
import se.eoslund.piggest.model.SearchListData
import se.eoslund.piggest.services.YTApiClient
import se.eoslund.piggest.services.YTApiInterface
import se.eoslund.piggest.utilites.Constants.EOS_NEWS_URL
import se.eoslund.piggest.utilites.Constants.EOS_WEB_BASE_URL
import se.eoslund.piggest.utilites.Constants.YT_API_KEY
import se.eoslund.piggest.utilites.Constants.YT_CHANNEL_ID
import se.eoslund.piggest.utilites.Constants.YT_ORDER
import se.eoslund.piggest.utilites.Constants.YT_PART

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var pageNumber: Int = 1
    var newsList: ArrayList<News> = ArrayList()
    lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentNewsBinding
    var isLoading: Boolean = false

    private val ytMaxResult = 50

    var data: SearchListData? = null
    var playList: List<SearchListData.Items>? = null


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
    ): View {
        newsList.clear()
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        showLoadingAnimation(true)
        getNews()
        binding.newsRadioButton.isChecked = true
        binding.radioGroupNews.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.news_radio_button -> {
                    prepareList()
                    segmentControlStatus = NewsStatus.NEWS
                    pageNumber = 1
                    getNews()
                }
                R.id.video_radio_button -> {
                    prepareList()
                    segmentControlStatus = NewsStatus.VIDEO
                    pageNumber = 1
                    getVideo()
                }
            }
        }

        newsAdapter = NewsAdapter(newsList) {
            when(segmentControlStatus){
                NewsStatus.NEWS -> {
                    requireActivity()
                        .supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebStatsFragment.newInstance("$EOS_WEB_BASE_URL${it.link}"))
                        .addToBackStack(null)
                        .commit()
                }
                NewsStatus.VIDEO -> {
                    Intent(App.instance, YoutubePlayerFullScreen::class.java)
                        .putExtra("videoID", it.link)
                        .apply { startActivity(this) }
                }
            }
        }

        binding.newsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.newsList.adapter = newsAdapter

        binding.newsList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoading = true
                    pageNumber++
                    when (segmentControlStatus) {
                        NewsStatus.NEWS -> getNews()
                        NewsStatus.VIDEO -> getVideo()
                    }
                }
            }
        })


        return binding.root

    }

    private fun prepareList() {
        newsList.clear()
        newsAdapter.notifyDataSetChanged()
        showLoadingAnimation(true)
    }

    private fun getNews() {
         lifecycleScope.launch {
             withContext(Dispatchers.IO) {
                 val url = "$EOS_NEWS_URL$pageNumber"
                 getNewsHtml(url)
             }
             showLoadingAnimation(false)
             isLoading = false
             newsAdapter.notifyDataSetChanged()
         }
     }

    private fun getVideo() {
        val apiInterface = YTApiClient.client?.create(YTApiInterface::class.java)
        val apiCall = apiInterface?.getList(YT_API_KEY, YT_CHANNEL_ID, YT_PART, YT_ORDER, ytMaxResult)
        apiCall?.enqueue(object : retrofit2.Callback<SearchListData> {
            override fun onFailure(call: retrofit2.Call<SearchListData>, t: Throwable) {
                println("Error: ${t.message}")
            }

            override fun onResponse(call: retrofit2.Call<SearchListData>, response: retrofit2.Response<SearchListData>) {
                data = response.body()
                playList = data?.items
                newsList.clear()
                playList?.forEach {

                    val title = it.snippet?.title ?: ""
                    val description = it.snippet?.description ?: ""
                    val imageLink = it.snippet?.thumbnails?.medium?.url ?: ""
                    val link = it.id?.videoId ?: ""
                    val publishedTime = it.snippet?.publishedAt ?: ""
                    val kind = it.id?.kind ?: ""
                    if (kind == "youtube#video") {
                            newsList.add(
                                se.eoslund.piggest.model.News(
                                    title,
                                    description,
                                    imageLink,
                                    publishedTime,
                                    link
                                )
                            )
                        }

                }
                showLoadingAnimation(false)
                newsAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun getNewsHtml(url: String) {
        val doc = Jsoup.connect(url).get()
        parseHtml(doc)
    }

    private fun parseHtml(doc: org.jsoup.nodes.Document) {
        val newsCards = doc.getElementsByAttributeValue("class", "card mt-4")
        newsCards.forEach() {
            val news = News()
            news.header = it.getElementsByClass("article-header").first()?.text() ?: ""
            news.content = it.getElementsByAttributeValue("class", "mt-3").first()?.text() ?: ""
            news.imageLink = it.getElementsByAttributeValue("class", "img-fluid").attr("src")
                ?: "https://www.eoslund.se/eos/svg/eos-logo.svg"
            news.link = it.getElementsByAttributeValue("class", "float-right").attr("href")
                ?: "https://www.eoslund.se/404"
            news.date = it.getElementsByAttributeValue("class", "date").first()?.text() ?: ""
            newsList.add(news)
        }
    }

    private fun showLoadingAnimation(show: Boolean) {
        if (show && newsList.isEmpty()) {
            binding.newsLoadingAnimation.visibility = View.VISIBLE
        } else {
            binding.newsLoadingAnimation.visibility = View.GONE
        }

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var segmentControlStatus: NewsStatus = NewsStatus.NEWS
    }
}

enum class NewsStatus {
    NEWS, VIDEO
}