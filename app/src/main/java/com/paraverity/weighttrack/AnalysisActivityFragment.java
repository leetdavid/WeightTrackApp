package com.paraverity.weighttrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import static com.paraverity.weighttrack.MainActivity.age;
import static com.paraverity.weighttrack.MainActivity.editor;
import static com.paraverity.weighttrack.MainActivity.gender;
import static com.paraverity.weighttrack.MainActivity.height;
import static com.paraverity.weighttrack.MainActivity.preferences;
import static com.paraverity.weighttrack.MainActivity.weight;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class AnalysisActivityFragment extends Fragment {

    public static final String TAG = "TAG";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static TextView genderView;
    private static TextView weightView;
    private static TextView heightView;
    private static TextView ageView;

    private static TextView bmiView;
    private static TextView bmrView;

    public AnalysisActivityFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AnalysisActivityFragment newInstance(int sectionNumber) {
        AnalysisActivityFragment fragment = new AnalysisActivityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analysis, container, false);

        //update all fields in this section
        //gender
        gender = preferences.getString("gender", getResources().getString(R.string.text_male));
        genderView = (TextView)rootView.findViewById(R.id.valueGender);
        genderView.setText(gender);

        //weight
        weight = preferences.getFloat("weight", 60.0F);
        weightView = (TextView)rootView.findViewById(R.id.valueWeight);
        weightView.setText("" + weight + "kg");

        //height
        height = preferences.getFloat("height", 170.0F);
        heightView = (TextView)rootView.findViewById(R.id.valueHeight);
        heightView.setText("" + height + "cm");

        //age
        age = preferences.getInt("age", 20);
        ageView = (TextView)rootView.findViewById(R.id.valueAge);
        ageView.setText("" + age);

        bmiView = (TextView)rootView.findViewById(R.id.valueBMI);
        bmrView = (TextView)rootView.findViewById(R.id.valueBMR);

        //set onclick listener on Gender button, and create an alertdialog
        /*
        rootView.findViewById(R.id.gender).setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.dialog_gender_message)
                                //.setTitle(R.string.dialog_gender_title)
                                .setNegativeButton(R.string.text_male,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int id){
                                        updateGender(getResources().getString(R.string.text_male));
                                    }
                                })
                                .setPositiveButton(R.string.text_female,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int id){
                                        updateGender(getResources().getString(R.string.text_female));
                                    }
                                });

                        builder.create();
                        builder.show();
                    }
                }
        );
        */
        rootView.findViewById(R.id.gender).setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.pick_gender)
                                .setItems(R.array.genders_array, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        switch(which){
                                            case 0:
                                                updateGender(getResources().getString(R.string.text_male));
                                                break;
                                            case 1:
                                                updateGender(getResources().getString(R.string.text_female));
                                                break;
                                            default:
                                        }
                                    }
                                });

                        builder.create();
                        builder.show();
                    }
                }
        );

        //weight
        rootView.findViewById(R.id.weight).setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.dialog_weight_title);
                        final EditText input = new EditText(getContext());
                        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        input.setRawInputType(Configuration.KEYBOARD_12KEY);
                        input.setSingleLine();
                        //create a linear layout to have padding for weight
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        layout.setPadding(64,0,64,0);
                        layout.addView(input);
                        builder.setView(layout)
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        try{
                                            updateWeight(Float.parseFloat(input.getText().toString()));
                                        } catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                    }
                }
        );

        //height
        rootView.findViewById(R.id.height).setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        final EditText input = new EditText(getContext());
                        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        input.setRawInputType(Configuration.KEYBOARD_12KEY);
                        input.setSingleLine();
                        //create a linear layout to have padding for weight
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER_HORIZONTAL);
                        layout.setPadding(64,0,64,0);
                        layout.addView(input);
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.dialog_height_title)
                                .setView(layout)
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        try{
                                            updateHeight(Float.parseFloat(input.getText().toString()));
                                        } catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which){
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
        );

        //age
        rootView.findViewById(R.id.age).setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        final NumberPicker picker = new NumberPicker(getContext());
                        picker.setMinValue(1);
                        picker.setMaxValue(150);
                        picker.setValue(age);
                        picker.setWrapSelectorWheel(false);
                        final FrameLayout layout = new FrameLayout(getContext());
                        layout.addView(picker, new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER
                        ));
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.dialog_age_title)
                                .setView(layout)
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        updateAge(picker.getValue());
                                    }
                                })
                                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichi){
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
                }
        );

        doAnalysis();

        return rootView;
    }

    protected void updateGender(String g){
        gender = g;
        genderView.setText(g);
        editor.putString("gender", g);
        editor.apply();
        doAnalysis();
    }

    protected void updateWeight(float w){
        weight = w;
        weightView.setText("" + w + "kg");
        editor.putFloat("weight", w);
        editor.apply();
        doAnalysis();
    }

    protected void updateHeight(float h){
        height = h;
        heightView.setText("" + h + "cm");
        editor.putFloat("height", h);
        //fagit
        editor.apply();
        doAnalysis();
    }

    protected void updateAge(int a){
        age = a;
        ageView.setText("" + a);
        editor.putInt("age", a);
        editor.apply();
        doAnalysis();
    }

    protected void doAnalysis() {
        //BMI
        float bmi = 10000F * weight / height / height;
        String bmiStr = "" + cleanDecimal(bmi);
        //bmiStr = bmiStr.substring(0, bmiStr.indexOf(".") + 2);
        if (bmi < 18.5) {
            bmiStr += ": Underweight";
        } else if (bmi < 24.9) {
            bmiStr += ": Normal";
        } else if (bmi < 29.9) {
            bmiStr += ": Overweight";
        } else {
            bmiStr += ": Obese";
        }
        bmiView.setText(bmiStr);

        //BMR
        float bmr = 10 * weight + 6.25F * height - 5 * age;
        if (gender.equals(getResources().getString(R.string.text_male))) {
            bmr += 5;
        } else {
            bmr += -161;
        }
        bmrView.setText(cleanDecimal(bmr) + "kcal");


    }

    //returns a float with up to 2 decimals.
    private String cleanDecimal(float f){
        /*
        int i = (int)(f*100);
        String str = "" + (1.00F * i / 100);
        while(str.substring(str.indexOf(".")).length() < 2) str += "0";
        return str;
        */
        return String.format("%.2f", f);
    }

}