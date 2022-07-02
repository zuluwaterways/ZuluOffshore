package com.zulu.offshore;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    //private static Context bc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //bc=this.getParent();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        public static void closePrefs(){

        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference preference = findPreference("deleteDB");
            String base = (requireContext().getExternalFilesDirs(null)==null||requireContext().getExternalFilesDirs(null).length<1)?requireContext().getFilesDir().getAbsolutePath():requireContext().getExternalFilesDirs(null)[requireContext().getExternalFilesDirs(null).length-1].getAbsolutePath();
            final String satDatabase = base+"/ZuluBase.sqlite";
            final String streetDatabase = base+"/ZuluStreet.sqlite";
            final String overlayDatabase = base+"/ZuluOverlay.sqlite";

            File file = new File(satDatabase);
            if(file.exists()){
                int file_size = Integer.parseInt(String.valueOf(file.length()/1024))-9400;
                preference.setSummary("This will free up "+(int)(file_size/1024.0*100)/100.0+"mb");
            }


            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle(R.string.delete_title)
                            .setMessage(R.string.delect_confirm)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Log.d("here","deleting maps");
                                    try {
                                        SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(satDatabase, null);
                                        int satdel = sqliteDB.delete("tiles", "key>6200", null);
                                        sqliteDB.execSQL("VACUUM");
                                        SQLiteDatabase sqliteDB2 = SQLiteDatabase.openOrCreateDatabase(streetDatabase, null);
                                        int streetdel = sqliteDB2.delete("tiles", "key>6200", null);
                                        sqliteDB2.execSQL("VACUUM");
                                        SQLiteDatabase sqliteDB3 = SQLiteDatabase.openOrCreateDatabase(overlayDatabase, null);
                                        int overdel = sqliteDB3.delete("tiles", "key>6200", null);
                                        sqliteDB3.execSQL("VACUUM");
                                        Toast.makeText(getContext(), "Deleted " + (satdel + streetdel+overdel) + " tiles", Toast.LENGTH_LONG).show();
                                        File file = new File(satDatabase);
                                        if(file.exists()){
                                            int file_size = Integer.parseInt(String.valueOf(file.length()/1024))-9400;
                                            findPreference("deleteDB").setSummary(getString(R.string.free_up)+(int)(file_size/1024.0*100)/100.0+"mb");
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return false;
                }


            });
            Preference forcesync = findPreference("forceSync");
            forcesync.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DatabaseHelper mDbHelper = MainActivity.pdb;
                    mDbHelper.SyncData("eUiMmPFseUXhgSNctbLmgycBH68Sfw7GDr4P2ksE","forced");
                    requireActivity().onBackPressed();
                    return false;
                }
            });
            Preference importp = findPreference("import");
            importp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.setType("*/*");
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                    startActivityForResult(chooseFile, 19);
                    return false;
                }
            });

        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Uri fileUri;
            switch (requestCode) {
                case 19:
                    if (resultCode == -1) {
                        Uri uri = data.getData();
                        String filePath = data.getData().getPath();

                        String name = "temp.sqlite";
                        File   f = new File(getActivity().getCacheDir(),name);

                        int maxBufferSize = 1 * 1024 * 1024;

                        try {
                            InputStream  inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                            Log.e("InputStream Size","Size " + inputStream);
                            int  bytesAvailable = inputStream.available();
//                    int bufferSize = 1024;
                            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            final byte[] buffers = new byte[bufferSize];

                            FileOutputStream outputStream = new FileOutputStream(f);
                            int read = 0;
                            while ((read = inputStream.read(buffers)) != -1) {
                                outputStream.write(buffers, 0, read);
                            }
                            Log.e("File Size","Size " + f.length());
                            inputStream.close();
                            outputStream.close();

                            f.getPath();
                            Log.e("File Path","Path " + f.getPath());
                            f.length();
                            Log.e("File Size","Size " + f.length());
                            SQLiteDatabase sqlitetemp = SQLiteDatabase.openOrCreateDatabase(f.getAbsolutePath(), null);

                            sqlitetemp.execSQL("update tiles set provider=\"USGS National Map Satellite\";");
                            Cursor res = sqlitetemp.rawQuery( "select count(*) as cnt from tiles", null );
                            res.moveToFirst();
                            int col = res.getColumnIndex("cnt");
                            if(col<0)col = 0;
                            String cnt = res.getString(col);
                            Log.e("count","ampunt: " + cnt);
                            res.close();
                            sqlitetemp.close();
                            //sqlitetemp.execSQL("ATTACH DATABASE '" + file.getAbsolutePath() + "' AS New_DB");
                            //sqlitetemp.execSQL("INSERT OR IGNORE INTO tiles SELECT * FROM New_DB.tiles ;");
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Import Maps")
                                    .setMessage("This will import "+cnt+" tiles into the the satellite layer do you want to continue?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Log.d("here","deleting maps");
                                            try {
                                                String base = (requireContext().getExternalFilesDirs(null)==null||requireContext().getExternalFilesDirs(null).length<1)?requireContext().getFilesDir().getAbsolutePath():requireContext().getExternalFilesDirs(null)[requireContext().getExternalFilesDirs(null).length-1].getAbsolutePath();
                                                final String satDatabase = base+"/ZuluBase.sqlite";
                                                SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(satDatabase, null);
                                                sqliteDB.execSQL("ATTACH DATABASE '" + f.getAbsolutePath() + "' AS New_DB");
                                                sqliteDB.execSQL("INSERT OR IGNORE INTO tiles SELECT * FROM New_DB.tiles ;");
                                                sqliteDB.close();
                                                Toast.makeText(getContext(), "File Imported!", Toast.LENGTH_LONG).show();

                                            }catch (Exception e){
                                                e.printStackTrace();
                                                Toast.makeText(getContext(), "Cant insert Database! There must be a key, provider and tile columns in the tiles table.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Cant read Database! There must be a key, provider and tile columns in the tiles table.", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    break;
            }
        }

    }
}