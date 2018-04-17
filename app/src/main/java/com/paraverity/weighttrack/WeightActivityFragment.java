package com.paraverity.weighttrack;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static com.paraverity.weighttrack.MainActivity.PREFS_NAME;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class WeightActivityFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public WeightActivityFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WeightActivityFragment newInstance(int sectionNumber) {
        WeightActivityFragment fragment = new WeightActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight, container, false);

        //create a listview
        MainActivity.weightListView = (ListView)rootView.findViewById(R.id.weight_listview);

		//sets onclick to listview
		MainActivity.weightListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						WeightEntry we  = (WeightEntry)parent.getAdapter().getItem(position);
						Log.d(PREFS_NAME, we.toDisplayMode().toString());
                        ((MainActivity)getActivity()).createAddRecordDialog(getActivity(), getContext(), we, we.getDate());
					}
				}
		);

		try {

			//create a listview
			MainActivity.weightListView = (ListView)rootView.findViewById(R.id.weight_listview);
			if(MainActivity.entries == null)
				MainActivity.entries = new ArrayList<>();
			else
				MainActivity.entries.clear();
			if(MainActivity.adapter == null)
				MainActivity.adapter = new WeightAdapter(getContext(), MainActivity.entries);
			MainActivity.weightListView.setAdapter(MainActivity.adapter);

			//get a cursor to fetch the data from the database
			String[] columns = {
					WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE(),
					WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_WEIGHT(),
					WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_POSITIVE(),
					WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NEGATIVE(),
					WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NOTES()
			};

			String sortOrder = WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE() + " DESC";

			SQLiteDatabase dbRead = MainActivity.getHelper(getContext()).getReadableDatabase();

			dbRead.beginTransaction();

			Cursor cursor = dbRead.query(
					WeightTrackDbHelper.SQLWeightEntry.Companion.getTABLE_NAME(),
					columns,
					null, //selection
					null, //selectionArgs
					null, //groupBy
					null, //having
					sortOrder, //orderBy
					null //limit
			);



			Log.d(PREFS_NAME, "Cursor reference obtained...");
			cursor.moveToFirst();
			Log.d(PREFS_NAME, "Cursor Moved to First Number....");

			if (cursor.getCount() > 0) {

				do {
					int date = cursor.getInt(cursor.getColumnIndex(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE()));
					float weight = cursor.getFloat(cursor.getColumnIndex(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_WEIGHT()));
					boolean pos = cursor.getInt(cursor.getColumnIndex(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_POSITIVE())) == 1;
					boolean neg = cursor.getInt(cursor.getColumnIndex(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NEGATIVE())) == 1;
					String notes = cursor.getString(cursor.getColumnIndex(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NOTES()));
					MainActivity.entries.add(WeightEntry.Companion.create(date, weight, pos, neg, notes));
					cursor.moveToNext();
				} while (!cursor.isAfterLast());

				cursor.close();
			}

			dbRead.setTransactionSuccessful();
			dbRead.endTransaction();
		} catch(Exception e){
			e.printStackTrace();
		}

		if(MainActivity.adapter != null)
			MainActivity.adapter.notifyDataSetChanged();
		if(MainActivity.weightChart != null)
			MainActivity.weightChart.notifyDataSetChanged();

		//MainActivity.createGraph(getContext(), MainActivity.weightChart);

        return rootView;
    }


}