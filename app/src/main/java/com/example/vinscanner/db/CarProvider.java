package com.example.vinscanner.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Ismael on 3/18/2017.
 */

public class CarProvider extends ContentProvider {

    public static final String LOG_TAG = CarProvider.class.getSimpleName();

    private static final int CARS = 100;

    private static final int CAR_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CarContract.CONTENT_AUTHORITY, CarContract.PATH_CARS, CARS);
        sUriMatcher.addURI(CarContract.CONTENT_AUTHORITY, CarContract.PATH_CARS + "/#", CAR_ID);
    }

    private CarDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new CarDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CARS:
                cursor = database.query(CarContract.CarEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CAR_ID:
                selection = CarContract.CarEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(CarContract.CarEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARS:
                return CarContract.CarEntry.CONTENT_LIST_TYPE;
            case CAR_ID:
                return CarContract.CarEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARS:
                return insertCar(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertCar(Uri uri, ContentValues values) {

        String make = values.getAsString(CarContract.CarEntry.COLUMN_CAR_MAKE);
        if (make == null) {
            throw new IllegalArgumentException("Car requires a make");
        }

        String model = values.getAsString(CarContract.CarEntry.COLUMN_CAR_MODEL);
        if (model == null) {
            throw new IllegalArgumentException("Car requires a model");
        }

        String year = values.getAsString(CarContract.CarEntry.COLUMN_CAR_YEAR);
        if (year == null) {
            throw new IllegalArgumentException("Car requires a year");
        }

        String vin = values.getAsString(CarContract.CarEntry.COLUMN_CAR_VIN);
        if (vin == null) {
            throw new IllegalArgumentException("Car requires a vin");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(CarContract.CarEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CarContract.CarEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CAR_ID:
                // Delete a single row given by the ID in the URI
                selection = CarContract.CarEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(CarContract.CarEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
