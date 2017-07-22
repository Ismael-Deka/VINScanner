package com.example.vinscanner.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ismael on 3/18/2017.
 */

public class CarContract {

    private CarContract() {

    }


    public static final String CONTENT_AUTHORITY = "com.example.vinscanner";


    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_CARS = "cars";


    public static final class CarEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CARS);


        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARS;


        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARS;


        public final static String TABLE_NAME = "cars";


        public final static String _ID = BaseColumns._ID;



        public final static String COLUMN_CAR_MAKE ="make";


        public final static String COLUMN_CAR_MODEL = "model";



        public final static String COLUMN_CAR_YEAR = "year";


        public final static String COLUMN_CAR_VIN = "vin";
    }

}
