package com.paraverity.weighttrack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paraverity.weighttrack.MainActivity.PREFS_NAME;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class ExportCSVTask extends AsyncTask<String, String, Boolean> {

	private Activity activity;
	private Context context;
	private File exportDir;
	private File exportFile;
	private ProgressDialog dialog;
	boolean memErr = false;

	public ExportCSVTask(Activity activity, Context context){
		super();
		this.activity = activity;
		this.context = context;
	}

	@Override
	protected void onPreExecute(){
		dialog = new ProgressDialog(context);
		dialog.setMessage("Exporting database...");
		dialog.show();
	}

	@Override
	protected Boolean doInBackground(final String... params) {
		boolean success = false;

		File dbFile = context.getDatabasePath(WeightTrackDbHelper.Companion.getDATABASE_NAME());
		Log.d(PREFS_NAME, "DB path is: " + dbFile);
		exportDir = new File(context.getFilesDir(), "export");

		long freeBytesInternal = new File(context.getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
		long megAvailable = freeBytesInternal / 1048576;

		String exportDirStr = "";

		if(megAvailable < 0.1){
			System.out.println("Please check" + megAvailable);
			memErr = true;
		} else {
			exportDirStr = exportDir.toString();
			Log.v(PREFS_NAME, "exportDir path: " + exportDir);
			if(exportDir.exists() == false){
				exportDir.mkdirs();
			}
			try{
				List<WeightEntry> entries = MainActivity.entries;

				CsvMapWriter writer = new CsvMapWriter(
						new FileWriter(exportDirStr + File.separator + Constants.File.CSV_FILE_NAME),
						CsvPreference.STANDARD_PREFERENCE);

				final String[] header = WeightTrackDbHelper.Companion.getHeaders();

				try {

					writer.writeHeader(header);

					final CellProcessor[] processors = {
							new UniqueHashCode(), //date
							new NotNull(), //weight
							new FmtBool("1", "0"), //positive
							new FmtBool("1", "0"), //negative
							new Optional() //notes
					};

					Map<String, Object> csvEntry;
					for (WeightEntry we : entries) {
						csvEntry = new HashMap<>();
						csvEntry.put(header[0], we.getDate());
						csvEntry.put(header[1], we.getWeight());
						csvEntry.put(header[2], we.getC1());
						csvEntry.put(header[3], we.getC2());
						csvEntry.put(header[4], we.getNotes());
						writer.write(csvEntry, header, processors);
					}

				} catch(Exception e){
					e.printStackTrace();
				} finally {
					if(writer != null){
						writer.close();
						success = true;
					}
				}



			} catch (IOException e){
				e.printStackTrace();
				Log.e(PREFS_NAME, e.getMessage(), e);
				return success;
			}
		}

		return success;
	}

	@Override
	protected void onPostExecute(Boolean success){
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		if(success){
			toast("Export Success! :)");
			exportFile = new File(exportDir, Constants.File.CSV_FILE_NAME);
			//exportFile = new File(exportDir.getAbsolutePath() + File.separator + Constants.File.CSV_FILE_NAME);
			Log.d(PREFS_NAME, "--------exportFile:" + exportFile.exists());

			Uri uri = FileProvider.getUriForFile(context, "com.paraverity.weighttrack.provider", exportFile);

			/*Intent intent = ShareCompat.IntentBuilder.from(activity)
					.setType("text/csv")
					.setStream(uri)
					.createChooserIntent()
					.addFlags();*/

			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			sharingIntent.setType(Constants.MIMETYPE_CSV);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
			context.startActivity(sharingIntent);
		} else {
			if(memErr){
				toast("You don't have enough space!");
			} else {
				toast("Export Failed! :(");
			}
		}
	}

	private void toast(String s){
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}
}