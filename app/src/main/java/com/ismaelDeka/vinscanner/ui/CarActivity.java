package com.ismaelDeka.vinscanner.ui;

import static com.ismaelDeka.vinscanner.R.id.fab;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ismaelDeka.vinscanner.CarLoader;
import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarInfoPagerAdapter;
import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.db.CarContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car>{

    private Uri mUri;
    private Car mCar;

    private String mVin;

    private TabLayout mTabs;
    private ViewPager mViewPager;

    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private LinearLayout mNoInternetView;

    private boolean mIsVehicleSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);



        mProgressBar = findViewById(R.id.progressBar);

        mFab = findViewById(fab);
        mTabs = findViewById(R.id.sliding_tabs);
        mViewPager = findViewById(R.id.viewpager);

        ImageButton mReloadButton = findViewById(R.id.reload_button);



        mNoInternetView = findViewById(R.id.empty_state);


        mProgressBar.setVisibility(View.VISIBLE);
        mNoInternetView.setVisibility(View.GONE);
        mFab.setVisibility(View.INVISIBLE);
        mTabs.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);

        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mNoInternetView.setVisibility(View.GONE);
                    getSupportLoaderManager().initLoader(1, null, CarActivity.this).forceLoad();
                }else{
                    mNoInternetView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);


                }
            }
        });

        if(savedInstanceState==null) {
            mVin = getIntent().getStringExtra("Vin");
        }else {
            mVin = savedInstanceState.getString("vin");
        }

        mIsVehicleSaved = isVehicleSaved();




    }

    @Override
    public void onStart(){
        if(isNetworkAvailable()) {
            if(mProgressBar.getVisibility() == View.VISIBLE) {
                getSupportLoaderManager().initLoader(1, null, this).forceLoad();
            }
        }else{
            mNoInternetView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);


        }
        super.onStart();

    }

    private void saveCar() {

        ContentValues values = new ContentValues();

        values.put(CarContract.CarEntry.COLUMN_CAR_MAKE, mCar.getMake());
        values.put(CarContract.CarEntry.COLUMN_CAR_MODEL, mCar.getModel());
        values.put(CarContract.CarEntry.COLUMN_CAR_YEAR, mCar.getYear());
        values.put(CarContract.CarEntry.COLUMN_CAR_VIN, mCar.getVin());

        Uri newUri = getContentResolver().insert(CarContract.CarEntry.CONTENT_URI, values);



        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, "Failed to Save",
                    Toast.LENGTH_SHORT).show();
        } else {
            if(getIntent().getBooleanExtra("restoreVehicle",false)){
                Toast.makeText(this, "Vehicle Restored",
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Saved",
                        Toast.LENGTH_SHORT).show();
            }
            saveCarLogo();

            mUri = newUri;
        }
    }
    private void saveCarLogo(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath=new File(directory,mCar.getMake()+".jpg");

        Bitmap bmp = mCar.getLogo();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void deleteCar(Uri uri) {

        if (uri!= null) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            int rowsDeleted = getContentResolver().delete(uri, null, null);

            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File myPath=new File(directory,mCar.getMake()+".jpg");
            boolean delete = myPath.delete();

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Failed to Delete Vehicle",
                        Toast.LENGTH_SHORT).show();
            } else if(delete) {
                Log.e("CarActivity", "Failed to Delete Vehicle Logo");
            }else {
                Intent intent = new Intent();
                intent.putExtra("make",mCar.getMake());
                intent.putExtra("model",mCar.getModel());
                intent.putExtra("year",mCar.getYear());
                intent.putExtra("vin",mVin);
                setResult(RESULT_OK,intent);
                finish();
            }
        }


    }
    private boolean validateVin(String errorCode){

        if(!errorCode.equals("0")){
            Toast toast = Toast.makeText(this, "Invalid VIN.", Toast.LENGTH_LONG);
            toast.show();
            startActivity(getParentActivityIntent());
            return false;
        }else{
            return true;
        }
    }

    private void exitFailedActivity(){
        Intent intent = new Intent();
        intent.putExtra("vin_failed",mVin);
        setResult(CommonStatusCodes.NETWORK_ERROR ,intent);
        finish();
    }

    private void displayVehicle(Car car){
        CarInfoPagerAdapter carInfoPagerAdapter = new CarInfoPagerAdapter(getSupportFragmentManager());
        carInfoPagerAdapter.setCar(car);
        mViewPager.setAdapter(carInfoPagerAdapter);
        mTabs.setTabTextColors(Color.BLACK, Color.WHITE);
        mTabs.setSelectedTabIndicatorColor(Color.WHITE);
        mTabs.setupWithViewPager(mViewPager);

       Objects.requireNonNull(getSupportActionBar()).setTitle(car.getYear() + " " + car.getMake() + " " + car.getModel());

    }

    @NonNull
    @Override
    public Loader<Car> onCreateLoader(int i, Bundle bundle) {
        return new CarLoader(this,mVin);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Car> loader, Car car) {

        if(car == null){
            exitFailedActivity();
        }else if(!car.isCarInfoAvailable()){
            exitFailedActivity();
        }else {

            if (validateVin(car.getErrorCode())) {

                mCar = car;

                if (mIsVehicleSaved) {
                    mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
                }

                mProgressBar.setVisibility(View.INVISIBLE);
                mFab.setVisibility(View.VISIBLE);
                if (mCar.getRecallInfo().size() > 0 && mCar.getComplaints().size() > 0) {
                    mTabs.setVisibility(View.VISIBLE);
                } else {
                    mTabs.setVisibility(View.GONE);
                }
                mViewPager.setVisibility(View.VISIBLE);

                displayVehicle(car);


                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mUri != null) {
                            deleteCar(mUri);
                        } else {
                            mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
                            saveCar();
                        }


                    }
                });

                if (getIntent().getBooleanExtra("restoreVehicle", false)) {
                    saveCar();
                }


            }

            getSupportLoaderManager().destroyLoader(1);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Car> loader) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isVehicleSaved(){
        Uri uri = CarContract.CarEntry.CONTENT_URI;
        String[] projection = {CarContract.CarEntry._ID, CarContract.CarEntry.COLUMN_CAR_VIN};
        String selection = CarContract.CarEntry.COLUMN_CAR_VIN + " like '" + mVin+"'";
        Cursor cursor = getContentResolver().query(uri,projection,selection,null,null);
        if (cursor != null&&cursor.getCount()>0) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(CarContract.CarEntry._ID);
            int id = cursor.getInt(idColumn);
            mUri = Uri.withAppendedPath(uri, id+"");
            return true;
        }else{
            if(cursor != null)
                cursor.close();
            return false;
        }
    }
}
