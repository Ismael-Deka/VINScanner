package com.example.vinscanner.ui;


import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinscanner.R;
import com.example.vinscanner.adapter.CarCursorAdapter;
import com.example.vinscanner.db.CarContract;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

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
    private CheckBox mSelectAllCheckBox;
    private LinearLayout mEmptyState;

    private boolean mIsInDeleteVehicleState = false;

    LinearLayout mSelectAllLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton scanButton = (FloatingActionButton) findViewById(R.id.scan_button);
        mCarList = (ListView) findViewById(R.id.car_list);
        mSelectAllLayout = (LinearLayout) findViewById(R.id.select_all_layout);

        mAdapter = new CarCursorAdapter(this,null);

        mCarList.setAdapter(mAdapter);

        mEmptyState = (LinearLayout) findViewById(R.id.empty_state);
        mCarList.setEmptyView(mEmptyState);

        mSelectAllCheckBox= (CheckBox) findViewById(R.id.select_all_checkbox);

        mSelectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox;


                    for(int i = 0; i < mCarList.getCount(); i++){
                        View item = mCarList.getChildAt(i);
                        checkBox =  (CheckBox) item.findViewById(R.id.car_list_checkbox);
                        if(mSelectAllCheckBox.isChecked()) {
                            MainActivity.this.getSupportActionBar().setTitle(mCarList.getCount()+ " selected");
                            checkBox.setChecked(true);
                        }else{
                            MainActivity.this.getSupportActionBar().setTitle("0 selected");
                            checkBox.setChecked(false);
                        }
                    }

            }
        });



        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // User may have declined earlier, ask Android if we should show him a reason


                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        // show an explanation to the user
                        // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                    } else {
                        // request the permission.
                        // CALLBACK_NUMBER is a integer constants
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAPTURE_IMAGE);
                        // The callback method gets the result of the request.
                    }
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
                startCarActivity(uri);

            }
        });
        mCarList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                setDeleteVehicleState();
                return true;
            }
        });





}
    private int getNumSelected(String str){
        int num;
        switch (str.length()-10){
            case 1:
                num= Integer.valueOf(str.substring(0,2));
                break;
            case 2:
                num= Integer.valueOf(str.substring(0,3));
                break;
            case 3:
                num= Integer.valueOf(str.substring(0,4));
                break;
            case 4:
                num= Integer.valueOf(str.substring(0,4));
                break;
            default:
                num = Integer.valueOf(str.substring(0,1));
        }
        return num;
    }
    private void setDeleteVehicleState(){
        mDeleteButton.setVisible(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        MainActivity.this.getSupportActionBar().setTitle("0 Selected");
        mSelectAllLayout.setVisibility(View.VISIBLE);
        CheckBox checkBox;
        for(int i = 0; i < mCarList.getCount(); i++){
            View item = mCarList.getChildAt(i);
            checkBox =  (CheckBox) item.findViewById(R.id.car_list_checkbox);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox c = (CheckBox) v;
                    String str = MainActivity.this.getSupportActionBar().getTitle().toString();
                    int num = getNumSelected(str);


                    if(c.isChecked()){
                        num++;
                    }else{
                        num--;
                    }MainActivity.this.getSupportActionBar().setTitle(num+str.substring(str.length()-9));
                    if(num==mCarList.getCount()){
                        mSelectAllCheckBox.setChecked(true);
                    }else {
                        mSelectAllCheckBox.setChecked(false);
                    }
                }
            });
        }
        mIsInDeleteVehicleState = true;

    }
    public void restoreNormalState(){
        mDeleteButton.setVisible(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        MainActivity.this.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        mSelectAllLayout.setVisibility(View.GONE);
        mSelectAllCheckBox.setChecked(false);
        CheckBox checkBox;
        for(int i = 0; i < mCarList.getCount(); i++){
            View item =  mCarList.getChildAt(i);
            checkBox =  (CheckBox) item.findViewById(R.id.car_list_checkbox);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        }
        mIsInDeleteVehicleState = false;

    }
    public void deleteVehicles(){
        CheckBox checkBox;
        for(int i = 0; i < mCarList.getCount(); i++){
            View item =  mCarList.getChildAt(i);
            checkBox =  (CheckBox) item.findViewById(R.id.car_list_checkbox);
            if(checkBox.isChecked()){
                long id = mCarList.getItemIdAtPosition(i);
                Uri uri = ContentUris.withAppendedId(CarContract.CarEntry.CONTENT_URI, id);
                getContentResolver().delete(uri, null, null);
            }
        }
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
        mDeleteButton.setVisible(false);
        mDeleteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int numSelected = getNumSelected(getSupportActionBar().getTitle().toString());
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteVehicles();
                                restoreNormalState();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                if(numSelected>0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to delete " + numSelected + " vehicles?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }

                return true;
            }
        });
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint("Enter Vehicle, or VIN...");
        mSearchView.setSubmitButtonEnabled(true);


        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Cursor cursor;
                if(constraint != null && constraint.length()>0)
                    cursor = getCursor(constraint.toString());
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
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
            case android.R.id.home:
                if(mIsInDeleteVehicleState)
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
                startCarActivity(null);
            }
        }else if(resultCode == RESULT_OK){
            final String make = data.getStringExtra("make");
            final String model = data.getStringExtra("model");
            final String year = data.getStringExtra("year");
            final String vin = data.getStringExtra("vin");
            Snackbar.make(mCarList,"Vehicle Deleted.", Snackbar.LENGTH_LONG).
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
    protected void onDestroy() {
        //mAdapter.getCursor().close();
        super.onDestroy();

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
            mVin = query.toUpperCase();
            startCarActivity(null);
        } else{
            Toast.makeText(this,"Vehicle not found.", Toast.LENGTH_SHORT).show();
            mSearchView.setQuery("",false);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
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


    private Cursor getCursor(String str) {

        Cursor mCursor;
        if (str == null  ||  str.length () == 0)  {
            mCursor = mAdapter.getCursor();
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
