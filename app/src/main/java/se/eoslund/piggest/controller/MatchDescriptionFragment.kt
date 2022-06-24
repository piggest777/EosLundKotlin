package se.eoslund.piggest.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.eoslund.piggest.R
import se.eoslund.piggest.databinding.FragmentWebStatsBinding
import se.eoslund.piggest.utilites.Constants.GAME_DESCRIPTION

class MatchDescriptionFragment : Fragment() {

    private var matchDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        matchDescription = arguments?.getString(GAME_DESCRIPTION)

        return if (matchDescription == null) {
            val view = inflater.inflate(R.layout.fragment_demo, container, false)
            val textView = view.findViewById<TextView>(R.id.fragment_name)
            textView.text = "No description is available"
            view
        } else {
            val view = inflater.inflate(R.layout.fragment_match_description, container, false)
            view
        }
    }
}