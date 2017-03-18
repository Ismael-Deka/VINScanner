package com.example.vinscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car>{

    private Car mCar;
    private CollapsingToolbarLayout mToolbarLayout;
    private String mVin;
    private TabLayout mTabDots;
    private ViewPager mGallery;
    private TextView mVinNumber;
    private TextView mDescription;
    private AppBarLayout mAppBarLayout;
    private ProgressBar mProgressBar;
    private CardView mCardView;
    private CardView mVinCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDescription = (TextView) findViewById(R.id.description);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mCardView = (CardView) findViewById(R.id.card);
        mVinNumber = (TextView) findViewById(R.id.vin_number);
        mVinCard = (CardView) findViewById(R.id.vin_card);
        mGallery = (ViewPager) findViewById(R.id.gallery);
        mTabDots = (TabLayout) findViewById(R.id.tabDots);
        TextView mNoInternetView = (TextView) findViewById(R.id.no_internet);


        mProgressBar.setVisibility(View.VISIBLE);
        mCardView.setVisibility(View.INVISIBLE);
        mNoInternetView.setVisibility(View.INVISIBLE);
        mVinCard.setVisibility(View.INVISIBLE);

        mVin = getIntent().getStringExtra("Vin");

        mAppBarLayout.setExpanded(false,false);
        if(isNetworkAvailable()) {
            getSupportLoaderManager().initLoader(1, null, this).forceLoad();
        }else{
            mNoInternetView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
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

            mProgressBar.setVisibility(View.INVISIBLE);
            mCardView.setVisibility(View.VISIBLE);
            mVinCard.setVisibility(View.VISIBLE);


            mToolbarLayout.setTitle(car.getYear()+" "+car.getMake()+" "+car.getModel());

            if(car.getCarImages() != null) {
                Bitmap[] galleryImages = car.getCarImages();
                CarImagePagerAdapter carImageAdapter = new CarImagePagerAdapter(CarActivity.this,galleryImages);

                mGallery.setAdapter(carImageAdapter);
                if(galleryImages.length > 1)
                    mTabDots.setupWithViewPager(mGallery);
                }

                mAppBarLayout.setExpanded(true,true);


            mVinNumber.setText("Vin: "+car.getVin());

            ArrayList<CarAttribute> attributes = car.getAttributes();
            String key;
            String value;

            for(int i = 3; i < attributes.size(); i++){
                key = attributes.get(i).getKey();
                value = attributes.get(i).getValue();
                mDescription.setText(mDescription.getText()+key+": "+value+"\n"+"\n");
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
}
