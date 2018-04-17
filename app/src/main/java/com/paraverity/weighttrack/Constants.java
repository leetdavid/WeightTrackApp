package com.paraverity.weighttrack;

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

public abstract class Constants {

	public static final int READ_REQUEST_CODE = 42;

	public static final int REQUEST_CODE_RESOLVE_CONNECTION = 20;

	public static final int BUFFER_SIZE = 4096;

	public static final int REQUEST_CODE_FILE_CHOOSE = 123;

	public static final String TAG = "weighttrack";

	public static final String MIMETYPE_SQLITE = "application/x-sqlite3";
	public static final String MIMETYPE_CSV = "text/csv";

	public static class File {
		public static final String DIR_NAME = "WeightTrack";
		public static final String CSV_FILE_NAME = "weightTrack.csv";
	}



}
