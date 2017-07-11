package com.example.vinscanner.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vinscanner.CarImagePagerAdapter;
import com.example.vinscanner.CarLoader;
import com.example.vinscanner.R;
import com.example.vinscanner.car.Car;
import com.example.vinscanner.db.CarContract;

import static com.example.vinscanner.R.id.fab;

public class CarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car>{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
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

        TextView mNoInternetView = (TextView) findViewById(R.id.no_internet);


        mProgressBar.setVisibility(View.VISIBLE);
        mNoInternetView.setVisibility(View.INVISIBLE);
        mFab.setVisibility(View.INVISIBLE);
        mTabs.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);

        mVin = getIntent().getStringExtra("Vin");




        mAppBarLayout.setExpanded(false,false);
        if(isNetworkAvailable()) {
            getSupportLoaderManager().initLoader(1, null, this).forceLoad();
        }else{
            mNoInternetView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

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

            Toast.makeText(this, "Saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCar(Uri uri) {

        if (uri!= null) {

            int rowsDeleted = getContentResolver().delete(uri, null, null);

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
            if(CarActivity.this.getIntent().getParcelableExtra("Uri") != null){
                mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
            }

            mProgressBar.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.VISIBLE);
            mTabs.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);


            CarInfoPagerAdapter carInfoPagerAdapter = new CarInfoPagerAdapter(getSupportFragmentManager());
            carInfoPagerAdapter.setCar(car);
            mViewPager.setAdapter(carInfoPagerAdapter);
            mTabs.setTabTextColors(Color.parseColor("#A8A19E"), Color.WHITE);
            mTabs.setSelectedTabIndicatorColor(Color.WHITE);
            mTabs.setupWithViewPager(mViewPager);

            mToolbarLayout.setTitle(car.getYear()+" "+car.getMake()+" "+car.getModel());

            if(car.getCarImages() != null) {
                Bitmap[] galleryImages = car.getCarImages();
                CarImagePagerAdapter carImageAdapter = new CarImagePagerAdapter(CarActivity.this,galleryImages);

                mGallery.setAdapter(carImageAdapter);
                if(galleryImages.length > 1)
                    mTabDots.setupWithViewPager(mGallery);
                }

                mAppBarLayout.setExpanded(true,true);

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri currentUri = CarActivity.this.getIntent().getParcelableExtra("Uri");
                    if(currentUri!=null){
                        deleteCar(currentUri);
                    }else{
                        mFab.setImageResource(R.drawable.ic_delete_forever_white_24dp);
                        saveCar();
                    }


                }
            });



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
}
