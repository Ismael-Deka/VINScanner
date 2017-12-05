package com.ismaelDeka.vinscanner.ui;


import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarCursorAdapter;
import com.ismaelDeka.vinscanner.db.CarContract;

import java.io.File;

;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>
                                                            ,SearchView.OnQueryTextListener {

    public static final String LOG_TAG = MainActivity.class.getName();

    private static final int PERMISSIONS_REQUEST_CAPTURE_IMAGE = 1;

    private SearchView mSearchView;

    private MenuItem mSearchMenuItem;

    private CarCursorAdapter mAdapter;


    private MenuItem mDeleteButton;

    private String mVin;

    private ListView mCarList;
    private LinearLayout mEmptyState;

    private boolean mDeleteVehicleState = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton scanButton = (FloatingActionButton) findViewById(R.id.scan_button);
        mCarList = (ListView) findViewById(R.id.car_list);


        mAdapter = new CarCursorAdapter(this,null);

        mCarList.setAdapter(mAdapter);

        mEmptyState = (LinearLayout) findViewById(R.id.empty_state);
        mCarList.setEmptyView(mEmptyState);





        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAPTURE_IMAGE);


                } else {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                        Intent cameraIntent = new Intent(MainActivity.this,VinScannerActivity.class);
                        startActivityForResult(cameraIntent, 1888);
                    }else{
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;

                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Failed to connect Camera").setPositiveButton("Ok", dialogClickListener)
                                .setTitle(getResources().getString(R.string.app_name)).show();
                    }

                }

            }
        });

        getLoaderManager().initLoader(0, null, this);

        mCarList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vinTextView = (TextView)view.findViewById(R.id.car_vin);
                mVin = vinTextView.getText().toString();

                Uri uri = ContentUris.withAppendedId(CarContract.CarEntry.CONTENT_URI, id);
                startCarActivity(uri,false);

            }
        });
        mCarList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                setDeleteVehicleState();
                return true;
            }
        });

        if(savedInstanceState != null){
            mDeleteVehicleState = savedInstanceState.getBoolean("deleteState");
        }


}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("deleteState", mDeleteVehicleState);

    }


    private void setDeleteVehicleState(){
        mDeleteButton.setVisible(false);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Delete Vehicles");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        mAdapter.setDeleteState(true);
        mDeleteVehicleState = true;

    }
    public void restoreNormalState(){
        mDeleteVehicleState = false;
        ActionBar actionBar = getSupportActionBar();
        mDeleteButton.setVisible(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        mAdapter.setDeleteState(false);


    }



    private void deleteCarLogo(String make) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, make + ".jpg");
        mypath.delete();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAPTURE_IMAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    Intent cameraIntent = new Intent(MainActivity.this,VinScannerActivity.class);
                    startActivityForResult(cameraIntent, 1888);


                } else {
                    // permission denied

                    Log.d("", "permission denied");
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.search);
        mDeleteButton = menu.findItem(R.id.delete);

        mDeleteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                setDeleteVehicleState();
                return true;
            }
        });

        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint("Enter Vehicle, or VIN...");
        mSearchView.setSubmitButtonEnabled(false);



        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Cursor cursor;
                if(constraint != null && constraint.length()>0)
                    cursor = getQueryCursor(constraint.toString());
                else
                    cursor = restoreCursor();

                return cursor;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchView.setQuery("",false);
                return true;
            }
        });

        mSearchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);

        if(mDeleteVehicleState){
            setDeleteVehicleState();
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDeleteVehicleState)
                    restoreNormalState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                startCarActivity(null,false);
            }
        }else if(resultCode == RESULT_OK){
            final String make = data.getStringExtra("make");
            final String model = data.getStringExtra("model");
            final String year = data.getStringExtra("year");
            final String vin = data.getStringExtra("vin");
            Snackbar.make(mCarList,year+" "+make+ " "+model+" Deleted.", Snackbar.LENGTH_INDEFINITE).
                    setAction("Undo", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mVin = vin;
                            startCarActivity(null,true);
                        }

                    }).show();
        }else if(resultCode == CommonStatusCodes.NETWORK_ERROR){
            mVin = data.getStringExtra("vin_failed");
            Snackbar.make(mCarList,"Failed to retrieve Vehicle Information.",Snackbar.LENGTH_INDEFINITE).
                    setAction("Retry", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startCarActivity(null,false);
                        }

                    }).show();
        }
    }


    private void startCarActivity(final Uri uri, boolean restore){
        if(mVin != null){
            Intent carIntent = new Intent(MainActivity.this,CarActivity.class);
            carIntent.putExtra("Vin",mVin);
            carIntent.putExtra("Uri", uri);
            carIntent.putExtra("restoreVehicle", restore);

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
    public boolean onQueryTextSubmit(String query) {
        Cursor c = getVinCursor(query);
        if(c != null){
            int vinIndex = c.getColumnIndex(CarContract.CarEntry.COLUMN_CAR_VIN);
            if(c.getCount()>0){
                Log.e(LOG_TAG,vinIndex+"");
                mVin = c.getString(vinIndex);
                c.close();
                startCarActivity(null,false);
            }else{
                Toast.makeText(this,"Vehicle not found.", Toast.LENGTH_SHORT).show();
                mSearchView.setQuery("",false);
            }
        }else if(query.length() == 17) {
            mVin = query.toUpperCase();
            startCarActivity(null,false);
        } else{
            Toast.makeText(this,"Vehicle not found.", Toast.LENGTH_SHORT).show();
            mSearchView.setQuery("",false);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(mDeleteVehicleState) {
            restoreNormalState();
        }
        mAdapter.getFilter().filter(newText);
        mAdapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(newText)) {
            mCarList.clearTextFilter();
            mAdapter.swapCursor(restoreCursor());
            mCarList.setEmptyView(mEmptyState);
        }
        else {
            mCarList.setEmptyView(null);
            mCarList.setFilterText(newText);

        }
        return true;
    }


    private Cursor getQueryCursor(String str) {

        Cursor cursor;
        if (str == null  ||  str.length () == 0)  {

            cursor = mAdapter.getCursor();

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
                    CarContract.CarEntry.COLUMN_CAR_MODEL + " like '" + query + "%'";
            }else {
                String[] query = str.split(" ");

                selection = CarContract.CarEntry.COLUMN_CAR_YEAR + " like '" +
                        query[0]+"%'" + " OR " + CarContract.CarEntry.COLUMN_CAR_MAKE + " like '% " + query[1] + " %'" + " OR " +
                        CarContract.CarEntry.COLUMN_CAR_MODEL + " like '% " + query[2] + " %'";

            }
            cursor = getContentResolver().query(uri, projection, selection, null,
                    null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
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
            mCursor = mAdapter.getCursor();
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

    private Cursor restoreCursor(){
        Uri uri =  CarContract.CarEntry.CONTENT_URI;
        String[] projection = new String[]{CarContract.CarEntry._ID,CarContract.CarEntry.COLUMN_CAR_YEAR,CarContract.CarEntry.COLUMN_CAR_MAKE
                ,CarContract.CarEntry.COLUMN_CAR_MODEL,CarContract.CarEntry.COLUMN_CAR_VIN};

        return getContentResolver().query(uri, projection, null, null,
                null);

    }

}
