package com.example.vinscanner.ui;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vinscanner.CarCursorAdapter;
import com.example.vinscanner.R;
import com.example.vinscanner.db.CarContract;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>
                                                            ,SearchView.OnQueryTextListener {

    public static final String LOG_TAG = MainActivity.class.getName();


    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private CarCursorAdapter mAdapter;

    private EditText mEditText;

    private String mVin;

    private ListView mCarList;

    private SimpleCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edit_text);
        Button enter = (Button) findViewById(R.id.enter);
        Button scanButton = (Button) findViewById(R.id.scan_button);
        Button clear = (Button) findViewById(R.id.clear);
        mCarList = (ListView) findViewById(R.id.car_list);

        mAdapter = new CarCursorAdapter(this,null);

        mCarList.setAdapter(mAdapter);


        mEditText.setText("JTNBE46K373015722");


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVin = mEditText.getText().toString();
                startCarActivity(null);

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                    Intent cameraIntent = new Intent(MainActivity.this,VinScannerActivity.class);
                    startActivityForResult(cameraIntent, 1888);
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main),
                            "Device doesn't have a camera.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        });

        getLoaderManager().initLoader(0, null, this);

        mCarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = parent.getItemAtPosition(position);
                Cursor cursor = (Cursor)o;
                int vinIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);
                mVin = cursor.getString(vinIndex);

                Uri uri = ContentUris.withAppendedId(CarContract.CarEntry.CONTENT_URI, id);
                startCarActivity(uri);

            }
        });



}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint("Enter Vehicle, or VIN...");

        String[] from = {CarContract.CarEntry.COLUMN_CAR_YEAR,CarContract.CarEntry.COLUMN_CAR_MAKE
                ,CarContract.CarEntry.COLUMN_CAR_MODEL,CarContract.CarEntry.COLUMN_CAR_VIN};
        int[] to = {R.id.year,R.id.make,R.id.model,R.id.vin};

        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.cursor_list_item,null,from,to,0);
        mCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Cursor cursor = getCursor(constraint.toString());

                return cursor;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Object o = mCursorAdapter.getItem(position);
                Cursor cursor = (Cursor)o;
                int yearIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_YEAR);
                int makeIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MAKE);
                int modelIndex = cursor.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_MODEL);


                String year = cursor.getString(yearIndex);
                String make = cursor.getString(makeIndex);
                String model = cursor.getString(modelIndex);

                mSearchView.setQuery(year+" "+make+" "+model,false);

                cursor.close();
                return true;
            }
        });
        mSearchView.setSuggestionsAdapter(mCursorAdapter);

        mSearchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);


        return true;
    }

    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS) {
            Barcode vinBarcode;
            if(data != null) {
                vinBarcode = data.getParcelableExtra("barcode");
                mVin = vinBarcode.displayValue;
                if(mVin.length()!=17){
                    mVin = vinBarcode.displayValue.substring(1);
                }
                startCarActivity(null);
            }
        }else if(resultCode == RESULT_OK){
            final String make = data.getStringExtra("make");
            final String model = data.getStringExtra("model");
            final String year = data.getStringExtra("year");
            final String vin = data.getStringExtra("vin");
            Snackbar.make(mEditText,"Vehicle Deleted.", Snackbar.LENGTH_LONG).
                    setAction("Undo", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Uri uri = restoreCar(make,model,year,vin);
                            startCarActivity(uri);
                        }

                    }).show();
        }
    }

    private Uri restoreCar(String make, String model, String year, String vin){

        ContentValues values = new ContentValues();
        values.put(CarContract.CarEntry.COLUMN_CAR_MAKE, make);
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, model);
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, year);
        values.put(CarContract.CarEntry.COLUMN_CAR_VIN, vin);

        return getContentResolver().insert(CarContract.CarEntry.CONTENT_URI, values);

    }

    private void startCarActivity(final Uri uri){
        if(mVin != null){
            Intent carIntent = new Intent(MainActivity.this,CarActivity.class);
            carIntent.putExtra("Vin",mVin);
            carIntent.putExtra("Uri", uri);
            startActivityForResult(carIntent,2);
        }


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        return new CursorLoader(this, CarContract.CarEntry.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        getLoaderManager().destroyLoader(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Cursor c = getVinCursor(query);
        if(c != null){
            int vinIndex = c.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);
            if(c.getCount()>0){
                Log.e(LOG_TAG,vinIndex+"");
                mVin = c.getString(vinIndex);
                c.close();
                startCarActivity(null);
            }else{
                Toast.makeText(this,"Vehicle not found.", Toast.LENGTH_SHORT).show();
                mSearchView.setQuery("",false);
            }
        }else if(query.length() == 17) {
            mVin = query;
            startCarActivity(null);
        } else{
            Toast.makeText(this,"Vehicle not found.", Toast.LENGTH_SHORT).show();
            mSearchView.setQuery("",false);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCursorAdapter.getFilter().filter(newText);
        mCursorAdapter.notifyDataSetChanged();
        if (TextUtils.isEmpty(newText)) {
               mCarList.clearTextFilter();
        }
        else {
            mCarList.setFilterText(newText.toString());
        }
        return true;
    }
    private Cursor getCursor(String str) {
        Cursor mCursor;
        if (str == null  ||  str.length () == 0)  {
            mCursor = mCursorAdapter.getCursor();
        }
        else {
            Uri uri =  CarContract.CarEntry.CONTENT_URI;
            String selection;
            String[] projection = new String[]{CarContract.CarEntry._ID,CarContract.CarEntry.COLUMN_CAR_YEAR,CarContract.CarEntry.COLUMN_CAR_MAKE
                    ,CarContract.CarEntry.COLUMN_CAR_MODEL,CarContract.CarEntry.COLUMN_CAR_VIN};
            if(str.split(" ").length<=1){
                str = str.replace(" ","");
                selection = CarContract.CarEntry.COLUMN_CAR_YEAR + " like '" +
                        str+"%'" + " OR " + CarContract.CarEntry.COLUMN_CAR_MAKE + " like '" + str + "%'" + " OR " +
                        CarContract.CarEntry.COLUMN_CAR_MODEL + " like '" + str + "%'"+" OR "+
                        CarContract.CarEntry.COLUMN_CAR_VIN + " like '" + str + "%'";
            }else if(str.split(" ").length == 2) {
                String[] query = str.split(" ");

                selection = CarContract.CarEntry.COLUMN_CAR_YEAR + " like '" +
                    query[0]+"%'" + " OR " + CarContract.CarEntry.COLUMN_CAR_MAKE + " like '% " + query[1] + " %'" + " OR " +
                    CarContract.CarEntry.COLUMN_CAR_MODEL + " like '" + str + "%'";
            }else {
                String[] query = str.split(" ");

                selection = CarContract.CarEntry.COLUMN_CAR_YEAR + " like '" +
                        query[0]+"%'" + " OR " + CarContract.CarEntry.COLUMN_CAR_MAKE + " like '% " + query[1] + " %'" + " OR " +
                        CarContract.CarEntry.COLUMN_CAR_MODEL + " like '% " + query[2] + " %'";

            }
            mCursor = getContentResolver().query(uri, projection, selection, null,
                    null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    private Cursor getVinCursor(String str) {
        Cursor mCursor;
        String[] carName = str.split(" ");
        if(carName.length <3)
            return null;
        String year = carName[0];
        String make = carName[1];
        String model = carName[2];

        if (str == null ||  str.length () == 0)  {
            mCursor = mCursorAdapter.getCursor();
        }
        else {
            Uri uri =  CarContract.CarEntry.CONTENT_URI;
            String[] projection = new String[]{CarContract.CarEntry._ID,CarContract.CarEntry.COLUMN_CAR_VIN};
            String selection = CarContract.CarEntry.COLUMN_CAR_YEAR + " like '" +
                    year+"%'" + " AND " + CarContract.CarEntry.COLUMN_CAR_MAKE + " like '" + make + "%'" + " AND " +
                    CarContract.CarEntry.COLUMN_CAR_MODEL + " like '" + model + "%'";
            String[] selectionArgs = null;
            String sortOrder = null;
            mCursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    sortOrder);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}
