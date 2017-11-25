package com.ismaelDeka.vinscanner.ui;

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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ismaelDeka.vinscanner.CarLoader;
import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarImagePagerAdapter;
import com.ismaelDeka.vinscanner.adapter.CarInfoPagerAdapter;
import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.db.CarContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.ismaelDeka.vinscanner.R.id.fab;
import static com.ismaelDeka.vinscanner.R.id.toolbar;

public class CarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car>{

    private Uri mUri;
    private Car mCar;
    private CollapsingToolbarLayout mToolbarLayout;
    private String mVin;
    private TabLayout mTabDots;
    private TabLayout mTabs;
    private ViewPager mViewPager;
    private ViewPager mGallery;
    private AppBarLayout mAppBarLayout;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private LinearLayout mNoInternetView;
    private ImageView mImageNotFound;
    private ImageButton mReloadButton;
    private boolean mIsVehicleSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mGallery = (ViewPager) findViewById(R.id.gallery);
        mTabDots = (TabLayout) findViewById(R.id.tabDots);
        mFab = (FloatingActionButton) findViewById(fab);
        mTabs = (TabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mImageNotFound = (ImageView)findViewById(R.id.image_not_found);
        mReloadButton = (ImageButton) findViewById(R.id.reload_button);



        mNoInternetView = (LinearLayout) findViewById(R.id.empty_state);


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



        mAppBarLayout.setExpanded(false,false);



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
    private String saveCarLogo(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,mCar.getMake()+".jpg");

        Bitmap bmp = mCar.getLogo();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void deleteCar(Uri uri) {

        if (uri!= null) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            int rowsDeleted = getContentResolver().delete(uri, null, null);

            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File mypath=new File(directory,mCar.getMake()+".jpg");
            mypath.delete();

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Failed to Delete Vehicle",
                        Toast.LENGTH_SHORT).show();
            } else {
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
    private boolean validateVin(int errorCode){

        if(errorCode != 0){
            Toast toast = Toast.makeText(this, "Invalid VIN.", Toast.LENGTH_LONG);
            toast.show();
            startActivity(getParentActivityIntent());
            return false;
        }else{
            return true;
        }
    }

    @Override
    public Loader<Car> onCreateLoader(int i, Bundle bundle) {
        return new CarLoader(this,mVin);
    }

    @Override
    public void onLoadFinished(Loader<Car> loader, Car car) {


        if(validateVin(car.getErrorCode())) {

            mCar = car;

            if(mIsVehicleSaved){
                mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.VISIBLE);
            if(mCar.getRecallInfo().size() > 0 &&mCar.getComplaints().size() > 0){
                mTabs.setVisibility(View.VISIBLE);
            }else{
                mTabs.setVisibility(View.GONE);
            }
            mViewPager.setVisibility(View.VISIBLE);





            CarInfoPagerAdapter carInfoPagerAdapter = new CarInfoPagerAdapter(getSupportFragmentManager());
            carInfoPagerAdapter.setCar(car);
            mViewPager.setAdapter(carInfoPagerAdapter);
            mTabs.setTabTextColors(Color.BLACK, Color.WHITE);
            mTabs.setSelectedTabIndicatorColor(Color.WHITE);
            mTabs.setupWithViewPager(mViewPager);

            mToolbarLayout.setTitle(car.getYear()+" "+car.getMake()+" "+car.getModel());

            if(car.getCarImages() != null) {
                Bitmap[] galleryImages = car.getCarImages();
                CarImagePagerAdapter carImageAdapter = new CarImagePagerAdapter(CarActivity.this,galleryImages);
                mAppBarLayout.setExpanded(true,true);
                mGallery.setAdapter(carImageAdapter);
                if(galleryImages.length > 1)
                    mTabDots.setupWithViewPager(mGallery);
                }else{
                mImageNotFound.setVisibility(View.VISIBLE);
            }



            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mUri!=null){
                        deleteCar(mUri);
                    }else{
                        mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
                        saveCar();
                    }


                }
            });

            if(getIntent().getBooleanExtra("restoreVehicle",false)){
                saveCar();
            }



        }

        getSupportLoaderManager().destroyLoader(1);

    }

    @Override
    public void onLoaderReset(Loader<Car> loader) {

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
