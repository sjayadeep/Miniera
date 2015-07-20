package com.artincodes.miniera.utils.recent;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.artincodes.miniera.MainActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jayadeep on 15/7/15.
 */
public class RecentDBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "Recent.db";
    public static final String TABLE_NAME = "Recent";
    public static final String COLUMN_MSG= "msg";
    public static final String COLUMN_PACKAGE = "package";
    Context mContext;

    public RecentDBHelper(Context context) {

        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + TABLE_NAME +
                        "(" +
                        COLUMN_PACKAGE + " text," +
                        COLUMN_MSG + " text" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }



    public boolean insertItem(String packageName,String message)
    {



        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_PACKAGE, COLUMN_MSG},
                COLUMN_PACKAGE + "=?" + " AND " + COLUMN_MSG + "=?",
                new String[]{packageName, message},
                null, null, null);

        Toast.makeText(mContext," COUNT" + cursor.getCount(),Toast.LENGTH_SHORT).show();

        if(cursor.getCount() <= 0){


            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_PACKAGE, packageName);
            contentValues.put(COLUMN_MSG, message);
            db.insert(TABLE_NAME, null, contentValues);
            Toast.makeText(mContext,"ITEM ADDED",Toast.LENGTH_SHORT).show();
            return false;
        }

        cursor.close();


        return true;
    }


    public Cursor getWhatsappRecentItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + TABLE_NAME +" where " + COLUMN_PACKAGE + " = 'com.whatsapp'", null);
    }

    public void clearRecent(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

    }
}
