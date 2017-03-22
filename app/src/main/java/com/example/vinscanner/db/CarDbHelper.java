package com.example.vinscanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ismael on 3/18/2017.
 */

public class CarDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = CarDbHelper.class.getSimpleName();


    private static final String DATABASE_NAME = "cars.db";

    private static final int DATABASE_VERSION = 1;
    public CarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CARS_TABLE =  "CREATE TABLE " + CarContract.CarEntry.TABLE_NAME + " ("
                + CarContract.CarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CarContract.CarEntry.COLUMN_CAR_MAKE + " TEXT NOT NULL, "
                + CarContract.CarEntry.COLUMN_CAR_MODEL + " TEXT NOT NULL, "
                + CarContract.CarEntry.COLUMN_CAR_YEAR + " TEXT NOT NULL, "
                + CarContract.CarEntry.COLUMN_CAR_VIN + " TEXT NOT NULL);";


        db.execSQL(SQL_CREATE_CARS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
