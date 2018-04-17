package com.paraverity.weighttrack

import android.app.AlertDialog
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */
class PrefsFragment : PreferenceFragment() {

    protected lateinit var driveHelper : GoogleDriveHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

        val prefClearTable = findPreference("pref_clear_table")
        prefClearTable.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.prefs_confirm_clear_table)
                    .setPositiveButton(R.string.button_ok) { dialog, which -> run{
                        val dbWrite = MainActivity.getHelper(null).writableDatabase
                        dbWrite.beginTransaction()
                        dbWrite.execSQL("DROP TABLE IF EXISTS " + WeightTrackDbHelper.SQLWeightEntry.TABLE_NAME)
                        dbWrite.execSQL(WeightTrackDbHelper.SQL_CREATE_ENTRIES)
                        dbWrite.setTransactionSuccessful()
                        dbWrite.endTransaction()
                        dbWrite.close()
                    } }
                    .setNegativeButton(R.string.button_cancel) { dialog, which -> dialog.dismiss() }
                    .show()

            true
        }

        val prefBackupTable = findPreference("pref_backup_table")
        prefBackupTable.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            backupTable()
            true
        }

        val prefRestoreTable = findPreference("pref_restore_table")
        prefRestoreTable.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(activity)
                    .setTitle("Restore the database from Drive?")
                    .setPositiveButton(R.string.button_ok) { dialog, which -> restoreTable() }
                    .setNegativeButton(R.string.button_cancel){ dialog, which -> dialog.dismiss() }
                    .show()
            true
        }

        val prefImportTable = findPreference("pref_import_csv")
        prefImportTable.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(activity)
                    .setTitle("Import a database? This will replace the current data!")
                    .setPositiveButton(R.string.button_ok) { dialog, which -> importTable() }
                    .setNegativeButton(R.string.button_cancel){ dialog, which -> dialog.dismiss() }
                    .show()
            true
        }

        val prefExportTable = findPreference("pref_export_csv")
        prefExportTable.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            exportTable()
            true
        }

    }

    fun exportTable(){
        //ExportCSVTask(activity, context)
    }

    fun importTable(){

    }

    fun backupTable(){
        //PrefsActivity.driveHelper.backup()
    }

    fun restoreTable(){
        //PrefsActivity.driveHelper.restore()
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): PrefsFragment {
            val prefs = PrefsFragment()
            return prefs
        }
    }


}
