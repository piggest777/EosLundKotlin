package se.eoslund.piggest.controller

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
    val url: String = "https://www.eoslund.se/om-eos/nyheter?page=$pageNumber"
    var newsList: ArrayList<News> = ArrayList()
    lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentNewsBinding

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

        binding = FragmentNewsBinding.inflate(inflater, container, false)
        newsAdapter = NewsAdapter(newsList) {

            val url = "https://www.eoslund.se/$it.link"
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, WebStatsFragment.newInstance("https://www.eoslund.se/${it.link}"))
                .commitNow()
        }
        binding.newsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.newsList.adapter = newsAdapter

//        Thread {
//            val doc = Jsoup.connect(url).get()
//           parseHtml(doc)
//
//        }.start()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                getHtml(url)
            }
            newsAdapter.notifyDataSetChanged()
        }
        return binding.root

    }

    private fun getHtml(url: String) {
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
    }
}