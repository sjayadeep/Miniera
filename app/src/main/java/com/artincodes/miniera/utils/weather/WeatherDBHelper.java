package com.artincodes.miniera.utils.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artincodes.miniera.MainActivity;

/**
 * Created by jayadeep on 17/7/15.
 */
public class WeatherDBHelper extends SQLiteOpenHelper{


    public static final String DATABASE_NAME = "Weather.db";
    public static final String TABLE_NAME = "weather_table";
    public static final String COLUMN_WEATHER= "weather";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LOCATION = "location";
    Context mContext;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_WEATHER + " integer," +
                        COLUMN_DESCRIPTION + " text," +
                        COLUMN_LOCATION + " text" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }


    public void insertWeather(int weather, String description,String location){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WEATHER, weather);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_LOCATION,location);
        db.insert(TABLE_NAME, null, contentValues);

        Log.i("WEATHER","UPDATED");

    }

    public Cursor getWeather(){

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }
}
