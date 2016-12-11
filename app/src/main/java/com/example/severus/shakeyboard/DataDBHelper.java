package com.example.severus.shakeyboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class DataDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "KeyboardData.db";
    public static final String FEATURES_TABLE_NAME = "features";
    public static final String FEATURES_COLUMN_ID = "id";
    public static final String FEATURES_COLUMN_PRESSURE = "pressure";
    public static final String FEATURES_COLUMN_VELOCITY = "velocity";
    public static final String FEATURES_COLUMN_DURATION = "duration";
    private static final String FILENAME = "features.csv";
    private static int file_no = 0;
    public DataDBHelper(Context context) {

        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + FEATURES_TABLE_NAME+
                        "(id integer primary key autoincrement, pressure float, velocity float, duration float)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS features");
        onCreate(db);
    }

    //count number of rows
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, FEATURES_TABLE_NAME);
        return numRows;
    }

    //add row to database
    public void add_feature(Save_feature save_feature,Context ctx){
        ContentValues values=new ContentValues();

        if((int)(Math.floor(save_feature.get_speed()))==0||save_feature.get_duration()>15)
            return;

        values.put("pressure",save_feature.get_pressure());
        values.put("velocity",save_feature.get_speed());
        values.put("duration",save_feature.get_duration());
        SQLiteDatabase db=getWritableDatabase();
        db.insert(FEATURES_TABLE_NAME,null,values);
        db.close();
        int row=numberOfRows();
        Log.d("Con","her "+row);
        if(row>=20){
            databasetoString(ctx);
            delete_all();
        }


    }

    //delete all rows of table
    public void delete_all(){
        SQLiteDatabase db=getWritableDatabase();
        String query="delete from " + FEATURES_TABLE_NAME;
        db.execSQL(query);
    }
    //deletes num_row rows from the beginning
    public void delete_feature(int num_row){
        SQLiteDatabase db=getWritableDatabase();
        String query="delete from " + FEATURES_TABLE_NAME +
                " where "+FEATURES_COLUMN_ID+" in (select "+ FEATURES_COLUMN_ID +" from "+ FEATURES_TABLE_NAME+" order by _id LIMIT " +String.valueOf(num_row)+ ");";
        db.execSQL(query);
    }

    public ArrayList<String> databasetoString(Context ctx){
        String dbString="",file_str="";
        String path=ctx.getDatabasePath(DataDBHelper.DATABASE_NAME).toString();
        Log.d("Database Path: ",path);
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT * FROM "+FEATURES_TABLE_NAME;
        //Cursor
        Cursor c=db.rawQuery(query,null);
        //move to first row
        c.moveToFirst();
        while(c.isAfterLast() == false){
            dbString="";
            dbString+=c.getString(c.getColumnIndex(FEATURES_COLUMN_PRESSURE))+",\t";
            dbString+=c.getString(c.getColumnIndex(FEATURES_COLUMN_VELOCITY))+",\t";
            dbString+=c.getString(c.getColumnIndex(FEATURES_COLUMN_DURATION))+"\n";
            array_list.add(dbString);
            c.moveToNext();
            Log.d("Database",dbString);
            file_str+=dbString;


        }
        writeToFile(file_str,ctx);
        return array_list;
    }

    private void writeToFile(String data,Context ctx) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data_path = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.example.severus.shakeyboard//databases//"+DATABASE_NAME;
                //File home=new File(sd,"//Shakeyboard//DataFiles//");
                String backupDBPath = "//Shakeyboard//DataFiles//";

                File currentDB = new File(data_path, currentDBPath);

                File backupDB = new File(sd, backupDBPath);

                if(!backupDB.exists()) {
                    backupDB.mkdirs();
                }

                File gpxfile = new File(backupDB,"KeyboardData"+file_no+".txt");
                file_no++;
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(data);
                writer.flush();
                writer.close();
                Log.d("writt","w2");
            }
        } catch (Exception e) {
            Log.d("writt","exception");
        }

        /*  try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            Log.d("writt","w2");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File I/O", "File write failed: " + e.toString());
        }*/

    }

}
