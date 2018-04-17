package com.paraverity.weighttrack;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public class GoogleDriveHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

	private Activity activity;
	private Context context;
	private GoogleApiClient mGoogleApiClient;

	public void init(Activity activity, Context context){
		this.activity = activity;
		this.context = context;

		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		if(connectionResult.hasResolution()){
			try{
				connectionResult.startResolutionForResult(activity, Constants.REQUEST_CODE_RESOLVE_CONNECTION);
			} catch(IntentSender.SendIntentException e){
				e.printStackTrace();
				Toast.makeText(context, "Connection cannot be make!", Toast.LENGTH_SHORT).show();
			}
		} else {

		}
	}

	private static byte[] fileToByteArray(File file){
		if (file != null){
			try{
				return inputStreamToByteArray(new FileInputStream(file));
			} catch(Exception ignore){}
		}
		return null;
	}

	private static byte[] inputStreamToByteArray(InputStream is){
		byte[] buf = null;
		BufferedInputStream bufIS = null;
		if(is != null)
			try {
				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
				bufIS = new BufferedInputStream(is);
				buf = new byte[Constants.BUFFER_SIZE];
				int cnt;
				while((cnt = bufIS.read(buf)) >= 0){
					byteBuffer.write(buf, 0, cnt);
				}
				buf = byteBuffer.size() > 0? byteBuffer.toByteArray() : null;
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(bufIS != null) bufIS.close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		return buf;
	}

	public void backup(){
		ExportCSVTask task = new ExportCSVTask(activity, context);
		task.execute();
	}

	public void restore(){
		ImportCSVTask task = new ImportCSVTask(activity, context, null);
		task.execute();
	}

	public void onResume() {
		if(mGoogleApiClient == null){
			//Create API client again and bind it
			mGoogleApiClient = new GoogleApiClient.Builder(context)
					.addApi(Drive.API)
					.addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();
			mGoogleApiClient.connect();
		}
	}

	public void onPause() {
		if(mGoogleApiClient != null){
			mGoogleApiClient.disconnect();
		}
	}

	protected void onStart(){
		mGoogleApiClient.connect();
	}

	private void toast(String s) { Toast.makeText(context, s, Toast.LENGTH_SHORT).show();}

	final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
		@Override
		public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
			if(driveContentsResult.getStatus().isSuccess() == false){
				toast("Error while trying to create new file content");
				return;
			}
			final DriveContents driveContents = driveContentsResult.getDriveContents();

			new Thread(){
				@Override
				public void run(){
					OutputStream os = driveContents.getOutputStream();
					InputStream is = null;
					try{
						//is = context.getContentResolver().openInputStream(uri);
						if(is != null){
							byte[] data = new byte[Constants.BUFFER_SIZE];
							while(is.read(data) != -1){
								os.write(data);
							}
						}
						is.close();
						os.close();
					} catch(IOException e){
						Log.e(Constants.TAG, e.getMessage());
					}

				}
			}.start();
		}
	};
}
