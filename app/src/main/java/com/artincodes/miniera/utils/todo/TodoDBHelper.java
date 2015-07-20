package com.artincodes.miniera.utils.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by jayadeep on 5/1/15.
 */

public class TodoDBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "toto_table";
    public static final String COLUMN_ID= "task_index";
    public static final String COLUMN_TASK = "task";
    Context mContext;

    public TodoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_ID + " integer," +
                        COLUMN_TASK + " text" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }


    public void insertTask(String task){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TASK, task);
        db.insert(TABLE_NAME, null, contentValues);

        Log.i("WEATHER", "UPDATED");

    }

    public Cursor getTask(){

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }
}
