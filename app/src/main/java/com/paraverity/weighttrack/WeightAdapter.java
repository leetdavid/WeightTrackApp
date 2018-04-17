package com.paraverity.weighttrack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class WeightAdapter extends ArrayAdapter<WeightEntry> {

    public WeightAdapter(Context context, ArrayList<WeightEntry> entries){
        super(context, R.layout.fragment_list_item, entries);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){

        WeightEntry.DisplayMode dm = getItem(position).toDisplayMode();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_list_item, parent, false);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.list_weight_date);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.list_weight_weight);
        TextView tvPos = (TextView) convertView.findViewById(R.id.list_weight_check1);
        TextView tvNeg = (TextView) convertView.findViewById(R.id.list_weight_check2);
        TextView tvNotes = (TextView) convertView.findViewById(R.id.list_weight_notes);

        tvDate.setText(dm.getDate());
        tvWeight.setText(dm.getWeight());
        tvPos.setText(dm.getC1());
        tvNeg.setText(dm.getC2());
        tvNotes.setText(dm.getNotes());

        return convertView;
    }
}
