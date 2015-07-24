package com.artincodes.miniera.utils.launcher;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artincodes.miniera.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jayadeep on 11/7/15.
 */
public class AppsDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Apps.db";
    public static final String TABLE_NAME = "Apps";
    public static final String COLUMN_INDEX= "grid_index";
    public static final String COLUMN_PACKAGE = "package";
    public static final String COLUMN_NAME = "name";
    Context mContext;

    public AppsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_INDEX + " integer," +
                        COLUMN_PACKAGE + " text," +
                        COLUMN_NAME + " text" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }



    public boolean insertApp  ()
    {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pacsList = MainActivity.packageManager.queryIntentActivities(mainIntent, 0);

        Collections.sort(pacsList, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo emp1, ResolveInfo emp2) {
                return emp1.loadLabel(MainActivity.packageManager).toString()
                        .compareToIgnoreCase(emp2.loadLabel(MainActivity.packageManager)
                                .toString());
            }
        });

        for (int i = 0; i < pacsList.size(); i++) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_INDEX, i);
            contentValues.put(COLUMN_PACKAGE, pacsList.get(i).activityInfo.packageName);
            contentValues.put(COLUMN_NAME, pacsList.get(i).loadLabel(MainActivity.packageManager).toString());
            db.insert(TABLE_NAME,null, contentValues);
        }




        return true;
    }

    public Cursor getApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    public void refreshDB(){

        Cursor c= this.getApps();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pacsList = MainActivity.packageManager.queryIntentActivities(mainIntent, 0);
        if (pacsList.size()!=c.getCount()){
            this.insertApp();
        }

    }

    public Cursor searchApps(String query){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = new String[1];
        args[0] = "%"+query+"%";

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME+" like ?", args);
        return cursor;

    }
}
