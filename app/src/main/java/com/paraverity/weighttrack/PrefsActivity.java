package com.paraverity.weighttrack;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class PrefsActivity extends Activity {

	public static GoogleDriveHelper driveHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		driveHelper = new GoogleDriveHelper();
		driveHelper.init(PrefsActivity.this, getApplicationContext());

		PrefsFragment prefsFragment = new PrefsFragment();

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, prefsFragment)
				.commit();

	}

	private void toast(String str){
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStart(){
		super.onStart();
		driveHelper.onStart();
	}

	@Override
	protected void onResume(){
		super.onResume();
		driveHelper.onResume();
	}

	@Override
	protected void onPause(){
		driveHelper.onPause();
		super.onPause();
	}

}