package com.paraverity.weighttrack


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

class StatsActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_stats, container, false)

        val entries = MainActivity.entries

        //set check 1 and check 2 percentage stats
        var c1Percentage = 0f
        var c2Percentage = 0f

        if (entries != null && entries.size > 0) {
            for (i in entries.indices) {
                if (entries[i].c1) c1Percentage++
                if (entries[i].c2) c2Percentage++
            }
            c1Percentage /= entries.size.toFloat()
            c2Percentage /= entries.size.toFloat()
        }

        (rootView.findViewById(R.id.stats_text_c1_percentage) as TextView).text = cleanDecimal(c1Percentage * 100, "%")
        (rootView.findViewById(R.id.stats_text_c2_percentage) as TextView).text = cleanDecimal(c2Percentage * 100, "%")


        //set start and current weights
        if (entries != null && entries.size > 0) {
            (rootView.findViewById(R.id.stats_text_start_weight) as TextView).text = cleanDecimal(entries[entries.size - 1].weight, "kg")
            (rootView.findViewById(R.id.stats_text_current_weight) as TextView).text = cleanDecimal(entries[0].weight, "kg")
        }

        return rootView
    }

    private fun cleanDecimal(f: Float, post: String): String {
        return String.format("%.2f", f) + post
    }

    companion object {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): StatsActivityFragment {
            val fragment = StatsActivityFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }

}
