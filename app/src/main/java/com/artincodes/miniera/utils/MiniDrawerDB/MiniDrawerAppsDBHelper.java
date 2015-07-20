package com.artincodes.miniera.utils.MiniDrawerDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by jayadeep on 25/6/15.
 */
public class MiniDrawerAppsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MiniDrawerApps.db";
    public static final String TABLE_NAME = "Apps";
    public static final String COLUMN_INDEX= "grid_index";
    public static final String COLUMN_PACKAGE = "package";
    public static final String COLUMN_NAME = "name";
    Context mContext;

    public MiniDrawerAppsDBHelper(Context context) {
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

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);

    }



    public boolean insertApp  (int index, String packageName,String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INDEX, index);
        contentValues.put(COLUMN_PACKAGE, packageName);
        contentValues.put(COLUMN_NAME, name);
        db.insert(TABLE_NAME,null, contentValues);
        return true;
    }

    public Cursor getMiniDrawerApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }

    public boolean deleteApp(int index) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_INDEX + " = ?", new String[]{index + ""});
        Cursor c = this.getMiniDrawerApps();
        c.moveToFirst();
        int i = 0;
        while (!c.isAfterLast()) {
            String packageArray = c.getString(1);
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_INDEX, i);
            db.update(TABLE_NAME,
                    contentValues,
                    COLUMN_PACKAGE + " = ?",
                    new String[]{packageArray});
            c.moveToNext();
            i++;

        }
        return true;

    }
        public boolean swapAppIndex(int from,int to){

            String firstAppName;
            String firstAppPackage;
            String secondAppName;
            String secondAppPackage;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select * from " + TABLE_NAME, null);
            c.moveToPosition(from);
            firstAppName = c.getString(2);
            firstAppPackage = c.getString(1);
            c.moveToPosition(to);
            secondAppName = c.getString(2);
            secondAppPackage = c.getString(1);

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, firstAppName);
            contentValues.put(COLUMN_PACKAGE,firstAppPackage);

            db.update(TABLE_NAME,
                    contentValues,
                    COLUMN_INDEX + " = ?",
                    new String[]{to + ""});
            contentValues.clear();

            contentValues.put(COLUMN_NAME, secondAppName);
            contentValues.put(COLUMN_PACKAGE,secondAppPackage);
            db.update(TABLE_NAME,
                    contentValues,
                    COLUMN_INDEX + " = ?",
                    new String[]{from+""});

            return true;

        }

    }

