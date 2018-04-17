package com.paraverity.weighttrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;


/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class ImportCSVTask extends AsyncTask<String, String, Boolean> {

	private static final String TAG = "weighttrack_import";

	private Activity activity;
	private Context context;
	private Uri importUri;
	private File importDir;
	private File importFile;
	private ProgressDialog dialog;

	private ArrayList<WeightEntry> createdEntries;

	private InputStream importInputStream;

	public ImportCSVTask(Activity activity, Context context, Uri uri){
		super();
		this.context = context;
		Log.d(TAG, "File URI is: " + uri.getPath());
		importUri = uri;

	}

	@Override
	protected void onPreExecute(){

		dialog = new ProgressDialog(context);
		dialog.setMessage("Importing database...");
		dialog.show();

		File dbFile = context.getDatabasePath(WeightTrackDbHelper.Companion.getDATABASE_NAME());


	}

	@Override
	protected Boolean doInBackground(String... strings) {
		boolean success = false;
		try{
			importInputStream = context.getContentResolver().openInputStream(importUri);
			createdEntries = new ArrayList<>();
			CsvMapReader reader = new CsvMapReader(
					new InputStreamReader(importInputStream),
					CsvPreference.STANDARD_PREFERENCE);
			final String[] header = WeightTrackDbHelper.Companion.getHeaders();
			final CellProcessor[] processors = {
					new UniqueHashCode(), //date
					new NotNull(), //weight
					new ParseBool("1", "0"), //positive
					new ParseBool("1", "0"), //negative
					new Optional() //notes
			};
			try{
				Map<String, Object> csvEntry;
				reader.getHeader(true);
				while((csvEntry = reader.read(header, processors)) != null) {
					int date = Integer.parseInt((String) csvEntry.get(header[0]));
					float weight = Float.parseFloat((String) csvEntry.get(header[1]));
					boolean c1 = (boolean)csvEntry.get(header[2]);
					boolean c2 = (boolean)csvEntry.get(header[3]);
					String notes = (String) csvEntry.get(header[4]);
					if(notes == null) notes = "";
					WeightEntry we = WeightEntry.Companion.create(date, weight, c1, c2, notes);
					createdEntries.add(we);
				}
				reader.close();
				success = true;
			} catch(IOException e){
				Log.e(TAG, e.getMessage());
			}
		} catch(Exception e){
			//toast("File not found!");
			e.printStackTrace();
			Log.e(TAG, "File not found! " + e.getMessage());
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean success){
		if(success){
			SQLiteDatabase dbWrite = MainActivity.getHelper(null).getWritableDatabase();
			dbWrite.beginTransaction();
			dbWrite.execSQL("DROP TABLE IF EXISTS " + WeightTrackDbHelper.SQLWeightEntry.Companion.getTABLE_NAME());
			dbWrite.execSQL(WeightTrackDbHelper.Companion.getSQL_CREATE_ENTRIES());

			MainActivity.entries.clear();
			for(WeightEntry we : createdEntries){
				MainActivity.entries.add(we);
				MainActivity.insertData(we);
			}

			dbWrite.setTransactionSuccessful();
			dbWrite.endTransaction();
			MainActivity.adapter.notifyDataSetChanged();
			MainActivity.createGraph(context, MainActivity.weightChart);
			toast("Import success!");
		} else {
			toast("Importing failed! :(");
		}
		if(dialog.isShowing())
			dialog.dismiss();
	}

	private void toast(String s){ Toast.makeText(context, s, Toast.LENGTH_SHORT).show(); }
}
