package com.paraverity.weighttrack;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "WeightTrack";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;

    //User's Statistics
    protected static String gender;
    protected static float weight;
    protected static float height;
    protected static int age;

	private String dateStr;

	protected static ListView weightListView;
	protected static ArrayList<WeightEntry> entries;
    protected static WeightAdapter adapter;
	protected static WeightEntry entry;

    protected static LineChart weightChart;

    private final Calendar calendar = Calendar.getInstance();
    protected static int year;
    protected static int month;
    protected static int day;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		//initialize database stuff
		getHelper(MainActivity.this);
		Log.d(PREFS_NAME, "Database created... " + getHelper(null).getDatabaseName());

		if(entries == null) entries = new ArrayList<>();

		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_charts, null);
		Log.d(PREFS_NAME, "MainActivity weightChart defined");
		weightChart = (LineChart)view.findViewById(R.id.weightChart);

		//initialize date
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);

		setContentView(R.layout.activity_main);

		preferences = getSharedPreferences(PREFS_NAME, 0);
		editor = preferences.edit();

		//Initializing Visual things
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

		//set the floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		assert fab != null;
		fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create DEFAULT add record alert dialog
                createAddRecordDialog(MainActivity.this, getApplicationContext(), null, -1);
            }
        });


    }

    protected void createAddRecordDialog(final Activity activity, final Context context, final WeightEntry we, int ID){

        LayoutInflater inflater = activity.getLayoutInflater();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View dialogView = inflater.inflate(R.layout.dialog_add_record, null);

        final TextView tvDateValue = ((TextView)dialogView.findViewById(R.id.dialog_date));
        tvDateValue.setText(WeightEntry.Companion.create(getDateInt(year, month, day), 0, true, true, "").toDisplayMode().getDate());
        tvDateValue.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
						new DatePickerDialog(MainActivity.this,
								new DatePickerDialog.OnDateSetListener(){
									@Override
									public void onDateSet(DatePicker picker, int year, int month, int day){
										((MainActivity)activity).setDate(year, month + 1, day);
										tvDateValue.setText(WeightEntry.Companion.create(getDateInt(year, month + 1, day), 0, true, true, "").toDisplayMode().getDate());
									}
								},
								calendar.get(Calendar.YEAR),
								calendar.get(Calendar.MONTH),
								calendar.get(Calendar.DAY_OF_MONTH))
								.show();

					}
                });

		//Dialog settings
        builder.setTitle(R.string.dialog_addrecord_title)
                .setView(dialogView)
                .setPositiveButton(R.string.button_ok, null)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int whichi){
                        dialog.cancel();
                    }
                });

		//Set input type of weight to number
		EditText input = (EditText)dialogView.findViewById(R.id.edittext_weight);
		input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		input.setRawInputType(Configuration.KEYBOARD_12KEY);
		input.setSingleLine();

		if(we != null){
			((TextView)dialogView.findViewById(R.id.dialog_date)).setText(we.toDisplayMode().getDate());
			((EditText)dialogView.findViewById(R.id.edittext_weight)).setText("" + we.getWeight());
			((CheckBox)dialogView.findViewById(R.id.dialog_checkbox1)).setChecked(we.getC1());
			((CheckBox)dialogView.findViewById(R.id.dialog_checkbox2)).setChecked(we.getC2());
			((EditText)dialogView.findViewById(R.id.edittext_notes)).setText(we.getNotes());
			dialogView.findViewById(R.id.dialog_date).setEnabled(false);
		} else {
			setTodayDateInt();
		}

        //prevent the OK button by closing (by overriding setPositiveButton)
        //and implementing this part
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener(){
            @Override
            public void onShow(DialogInterface di){
                Button button = ((AlertDialog)di).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){

						//setTodayDateInt();
						int d = getDateInt(year, month, day);
						float w;
						try {
							w = Float.parseFloat(((EditText) dialogView.findViewById(R.id.edittext_weight)).getText().toString());
						} catch (Exception e) {
							Toast.makeText(context, R.string.error_null_weight, Toast.LENGTH_SHORT).show();
							return;
						}
						boolean c1 = ((CheckBox) dialogView.findViewById(R.id.dialog_checkbox1)).isChecked(),
								c2 = ((CheckBox) dialogView.findViewById(R.id.dialog_checkbox2)).isChecked();
						String n = ((EditText) dialogView.findViewById(R.id.edittext_notes)).getText().toString();

						entry = WeightEntry.Companion.create(d, w, c1, c2, n);
						Log.d(PREFS_NAME, "Entry created: " + entry.toDisplayMode().toString());

						//save data in SQL
						if(we == null){
							long l = MainActivity.insertData(entry);
							Log.d(PREFS_NAME, "data saved, ID= " + l);
						} else {
							entry.setDate(we.getDate());
							Log.d(PREFS_NAME, "Entry created," + entry.toDisplayMode().toString());
							long l = MainActivity.replaceData(entry);
							Log.d(PREFS_NAME, "data saved, ID= " + l);
						}

						//update data in arraylist
						boolean exists = false;
						for(int i = 0; i < entries.size(); i++){
							if(entries.get(i).getDate() == entry.getDate()){
								entries.set(i, entry);
								exists = true;
								break;
							}
						}
						if(!exists){ //doesn't exist
							entries.add(entry);
							Collections.sort(entries);
						}

						adapter.notifyDataSetChanged();

						createGraph(MainActivity.this, weightChart);

						dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }

	void setDate(int year, int month, int dayOfMonth) {
		dateStr = "" + year + month + dayOfMonth;
		MainActivity.year = year;
		MainActivity.month = month;
		MainActivity.day = dayOfMonth;
		Log.d(PREFS_NAME, dateStr);
	}

	private int setTodayDateInt(){
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		String str = "" + year;
		str += (month < 10? "0" : "") +  month;
		str += (day < 10? "0" : "") + day;
		return Integer.parseInt(str);
	}

	private int getDateInt(int y, int m, int d){
		String str = "" + y;
		str += (m < 10? "0" : "") +  m;
		str += (d < 10? "0" : "") + d;
		return Integer.parseInt(str);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()){

			case R.id.action_backup_restore:

				Intent intent = new Intent(this, BackupActivity.class);
				startActivity(intent);

				return true;

			case R.id.action_export:
				//Toast.makeText(getApplicationContext(), R.string.not_available_yet, Toast.LENGTH_SHORT).show();
				int permissionCheck = ContextCompat.checkSelfPermission(
						MainActivity.this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if(permissionCheck != PackageManager.PERMISSION_GRANTED)
					if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
					{} else ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


				ExportCSVTask task = new ExportCSVTask(this, MainActivity.this);
				task.execute();
				return true;

			case R.id.action_import:

				int permissionCheck2 = ContextCompat.checkSelfPermission(
						MainActivity.this,
						Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if(permissionCheck2 != PackageManager.PERMISSION_GRANTED)
					if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
					{} else ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

				new AlertDialog.Builder(MainActivity.this)
						.setTitle("Import Confirm")
						.setMessage("Importing will replace the data you currently have!")
						.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent fileChooseIntent = new Intent(Intent.ACTION_GET_CONTENT);
								fileChooseIntent.addCategory(Intent.CATEGORY_OPENABLE);
								fileChooseIntent.setType(Constants.MIMETYPE_CSV);
								startActivityForResult(fileChooseIntent, Constants.REQUEST_CODE_FILE_CHOOSE);
							}
						})
						.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.show();

				return true;

			case R.id.action_settings:
				//Open settings menu
				Intent i = new Intent(this, PrefsActivity.class);
				startActivity(i);
				return true;
			case R.id.action_about:
				Intent i2 = new Intent(this, AboutActivity.class);
				startActivity(i2);
		}
		return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
				case 1:
					return ChartsActivityFragment.newInstance(position);
                case 0:
                    return WeightActivityFragment.newInstance(position);
                case 2:
                    return StatsActivityFragment.Companion.newInstance(position);
                case 3:
                    return AnalysisActivityFragment.newInstance(position);

                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.section_weight);
                case 1:
                    return getResources().getString(R.string.section_chart);
                case 2:
                    return getResources().getString(R.string.section_stats);
                case 3:
                    return getResources().getString(R.string.section_analysis);
            }
            return null;
        }
    }

    @Override
    protected void onStop(){


        editor.putString("gender", gender);
        editor.commit();

		getHelper(null).close();
		dbHelper = null;

		super.onStop();
    }

    @Override
	protected void onPause(){

    	editor.commit();

    	super.onPause();

	}

	@Override
	protected void onResume(){
		super.onResume();
	}

	@Override
	protected void onStart(){
		super.onStart();
		getHelper(MainActivity.this);
	}

	public static long insertData(WeightEntry entry) {

		SQLiteDatabase dbWrite = getHelper(null).getWritableDatabase();
		dbWrite.beginTransaction();

		//Create a new map of values
		ContentValues values = new ContentValues();
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE(), entry.getDate());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_WEIGHT(), entry.getWeight());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_POSITIVE(), entry.getC1());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NEGATIVE(), entry.getC2());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NOTES(), entry.getNotes());
		long l = -1;

		try {
			l = dbWrite.insertOrThrow(WeightTrackDbHelper.SQLWeightEntry.Companion.getTABLE_NAME(), null, values);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(l != -1) dbWrite.setTransactionSuccessful();
			dbWrite.endTransaction();
		}

		return l;

	}

	public static long replaceData(WeightEntry entry){

    	SQLiteDatabase dbWrite = getHelper(null).getWritableDatabase();
		dbWrite.beginTransaction();

		ContentValues values = new ContentValues();
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE(), entry.getDate());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_WEIGHT(), entry.getWeight());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_POSITIVE(), entry.getC1());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NEGATIVE(), entry.getC2());
		values.put(WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NOTES(), entry.getNotes());
		long l = -1;

		try {
			l = dbWrite.replace(WeightTrackDbHelper.SQLWeightEntry.Companion.getTABLE_NAME(), null, values);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(l != -1) dbWrite.setTransactionSuccessful();
			dbWrite.endTransaction();
		}

		return l;
	}

	public static Cursor queryData(Context context, int date){
		String[] columns = {
				WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_WEIGHT(),
				WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_POSITIVE(),
				WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NEGATIVE(),
				WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_NOTES()
		};

		String selection = WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE() + " = ?";
		String[] selectionArgs = { "" + date };

		String sortOrder = WeightTrackDbHelper.SQLWeightEntry.Companion.getCOLUMN_DATE() + " DESC";

		SQLiteDatabase dbRead = getHelper(context).getReadableDatabase();

		dbRead.beginTransaction();

		Cursor cursor = dbRead.query(
				WeightTrackDbHelper.SQLWeightEntry.Companion.getTABLE_NAME(),
				columns,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder
		);
		dbRead.setTransactionSuccessful();
		dbRead.endTransaction();
		return cursor;
	}

	public static List<Entry> chartEntries = new ArrayList<>();
	public static LineDataSet dataSet = new LineDataSet(chartEntries, "Weight");

	public void updateGraph(WeightEntry we){
		chartEntries.add(new Entry((we.getDate() - entries.get(entries.size() - 1).getDate()), we.getWeight()));
		Collections.sort(chartEntries, new EntryXComparator());
		dataSet.notifyDataSetChanged();
		weightChart.notifyDataSetChanged();
		weightChart.fitScreen();
		weightChart.invalidate();
	}

    public static void createGraph(Context context, LineChart chart){
		if(entries == null) return;
		if(entries.size() == 0) return;
        ArrayList<WeightEntry> entries = MainActivity.entries;

		if(chart != null){
			weightChart = chart;
		}

		chartEntries = new ArrayList<>();

		for(WeightEntry we : entries){
			Log.d("GRAPH", "" + (we.getDate() - entries.get(entries.size() - 1).getDate()));
			chartEntries.add(new Entry((we.getDate() - entries.get(entries.size() - 1).getDate()), we.getWeight()));
		}
		Collections.sort(chartEntries, new EntryXComparator());
		dataSet = new LineDataSet(chartEntries, "Weight");
		dataSet.setColors(R.color.colorPrimary, R.color.colorPrimaryDark);
		dataSet.setValueTextColor(R.color.colorAccent);
		dataSet.setDrawCircles(false);
		//dataSet.setCubicIntensity(0.3F);
		dataSet.setDrawFilled(true);
		dataSet.setFillColor(R.color.colorPrimary);

		LineData lineData = new LineData(dataSet);
		weightChart.animate();
		weightChart.setDrawGridBackground(false);

		weightChart.setData(lineData);
		weightChart.invalidate();
		weightChart.notifyDataSetChanged();

    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultData){
    	super.onActivityResult(requestCode, resultCode, resultData);
		if(requestCode == Constants.READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
			Uri uri = null;
			if(resultData != null){
				uri = resultData.getData();
				Log.i(PREFS_NAME, "Uri: " + uri.toString());
			}
		} else if(requestCode == Constants.REQUEST_CODE_FILE_CHOOSE && resultCode == RESULT_OK){
			Uri importUri = resultData.getData();
			ImportCSVTask task = new ImportCSVTask(this,MainActivity.this, importUri);
			task.execute();
		}
	}

	public void updateEverything(){
		createGraph(MainActivity.this, weightChart);
		Collections.sort(entries);
		Collections.sort(chartEntries, new EntryXComparator());
		weightListView.setAdapter(adapter);
		adapter.notifyDataSetInvalidated();
		adapter.notifyDataSetChanged();
		weightChart.invalidate();
		weightChart.notifyDataSetChanged();
	}

	private static WeightTrackDbHelper dbHelper;

	public static synchronized WeightTrackDbHelper getHelper(Context context){
		if(dbHelper == null)
			dbHelper = new WeightTrackDbHelper(context);
		return dbHelper;
	}
}
