package com.paraverity.weighttrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;

import static com.paraverity.weighttrack.MainActivity.PREFS_NAME;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class ChartsActivityFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";


    public ChartsActivityFragment() {
        // Required empty public constructor
    }

    public static ChartsActivityFragment newInstance(int sectionNumber) {
        ChartsActivityFragment fragment = new ChartsActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        //do init stuff here
        MainActivity.weightChart = (LineChart) rootView.findViewById(R.id.weightChart);
		Log.d(PREFS_NAME, "weightChart has been defined!");
		MainActivity.createGraph(getContext(), MainActivity.weightChart);

        return rootView;
    }

}
