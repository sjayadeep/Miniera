package com.artincodes.miniera.utils.widgetutils;

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
public class WidgetDBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Widget.db";
    public static final String TABLE_NAME = "widget_table";
    public static final String COLUMN_INDEX = "widget_index";
    public static final String COLUMN_WIDGET_ID = "widget_id";
    Context mContext;

    public WidgetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_INDEX + " integer," +
                        COLUMN_WIDGET_ID + " integer" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }


    public void insertWidget(int index, int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_WIDGET_ID, id);
        db.insert(TABLE_NAME, null, contentValues);


    }

    public Cursor getWidgets() {

        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);

    }

    public void clearDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

    }

    public boolean deleteWidget(int widgetID) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_WIDGET_ID + " = ?", new String[]{widgetID + ""});
//        Cursor c = this.getWidgets();
//        c.moveToFirst();
//        int i = 0;
//        while (!c.isAfterLast()) {
//            String widgetID = c.getString(1);
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(COLUMN_INDEX, i);
//            db.update(TABLE_NAME,
//                    contentValues,
//                    COLUMN_WIDGET_ID+ " = ?",
//                    new String[]{widgetID});
//            c.moveToNext();
//            i++;

//        }
        return true;

    }
}
