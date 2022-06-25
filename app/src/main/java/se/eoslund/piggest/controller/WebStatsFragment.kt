package se.eoslund.piggest.controller

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.TextView
import androidx.annotation.RequiresApi
import se.eoslund.piggest.R
import se.eoslund.piggest.databinding.FragmentWebStatsBinding
import se.eoslund.piggest.services.MyWebViewClient
import se.eoslund.piggest.utilites.Constants.STATISTIC_LINK

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebStatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentWebStatsBinding
    private var statsLink: String? = null

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
        // Inflate the layout for this fragment
         statsLink = arguments?.getString(STATISTIC_LINK)

        return if (statsLink == null) {
            val view = inflater.inflate(R.layout.fragment_demo, container, false)
            val textView = view.findViewById<TextView>(R.id.fragment_name)
            textView.text = "No statistic is available"
            view
        } else {
            binding = FragmentWebStatsBinding.inflate(inflater, container, false)
            binding.root
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (statsLink != null) {
            super.onViewCreated(view, savedInstanceState)
            with(binding.statsWebView) {
                loadUrl(statsLink!!)
                settings.javaScriptEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                webViewClient = MyWebViewClient(requireActivity().applicationContext, binding.progressBar, binding.errorAnim)
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WebStatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            WebStatsFragment().apply {
                arguments = Bundle().apply {
                    putString(STATISTIC_LINK, param1)

                }
            }
    }
}