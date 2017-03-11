package com.example.vinscanner;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CarActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car>{

    private Car mCar;
    private CollapsingToolbarLayout mToolbarLayout;
    private String mVin;
    private ImageView mImage;
    private TextView mDescription;
    private AppBarLayout mAppBarLayout;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        mToolbar = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDescription = (TextView) findViewById(R.id.description);
        mImage = (ImageView) findViewById(R.id.image);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);


        mProgressBar.setVisibility(View.VISIBLE);
        mToolbarLayout.setVisibility(View.INVISIBLE);
        mAppBarLayout.setVisibility(View.INVISIBLE);

        mVin = getIntent().getStringExtra("Vin");

        mImage.setImageResource(R.drawable.placeholder);
        getSupportLoaderManager().initLoader(1, null,this).forceLoad();
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
            mAppBarLayout.setVisibility(View.VISIBLE);
            mToolbarLayout.setVisibility(View.VISIBLE);

            mToolbarLayout.setTitle(car.getYear()+" "+car.getMake()+" "+car.getModel());
           // mToolbarLayout.setC
            mImage.setImageBitmap(car.getCarImage());
            mDescription.setText("Vin: "+car.getVin());

        }

        getSupportLoaderManager().destroyLoader(1);

    }

    @Override
    public void onLoaderReset(Loader<Car> loader) {

    }
}
