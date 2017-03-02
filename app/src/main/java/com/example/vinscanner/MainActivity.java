package com.example.vinscanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import static com.example.vinscanner.R.id.make;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car> {

    public static final String LOG_TAG = MainActivity.class.getName();



    private TextView mMake;
    private TextView mModel;
    private TextView mYear;
    private TextView mVinText;

    private EditText mEditText;
    private Button mButton;
    private Button mScanButton;

    private ProgressBar mCircle;

    private String mVin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMake = (TextView) findViewById(make);
        mModel = (TextView) findViewById(R.id.model);
        mYear = (TextView) findViewById(R.id.year);
        mVinText = (TextView) findViewById(R.id.vin);

        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.button);

        mScanButton =(Button) findViewById(R.id.scan_button);

        mCircle = (ProgressBar) findViewById(R.id.circle);
        mCircle.setVisibility(View.INVISIBLE);




        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newVin = mEditText.getText().toString();
                mVin = newVin;
                getSupportLoaderManager().initLoader(1, null, MainActivity.this).forceLoad();


            }
        });


        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent cameraIntent = new Intent(MainActivity.this,VinScannerActivity.class);
                    startActivityForResult(cameraIntent, 1888);

            }
        });

}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS) {
            Barcode vinBacode;
            if(data != null) {
                vinBacode = data.getParcelableExtra("barcode");
                mVin = vinBacode.displayValue;
                if(mVin.length()!=17){
                    mVin = vinBacode.displayValue.substring(1);
                }
                getSupportLoaderManager().initLoader(1, null, MainActivity.this).forceLoad();
            }
        }

    }
    private boolean validateVin(int errorCode){

        if(errorCode != 0){
            Toast toast = Toast.makeText(this, "Invalid VIN.", Toast.LENGTH_LONG);
            toast.show();
            return false;
        }else{
            return true;
        }
    }


    @Override
    public Loader<Car> onCreateLoader(int i, Bundle bundle) {
        mCircle.setVisibility(View.VISIBLE);
        return new CarLoader(MainActivity.this,mVin);
    }

    @Override
    public void onLoadFinished(Loader<Car> loader, Car car) {


        mCircle.setVisibility(View.INVISIBLE);

        if(validateVin(car.getErrorCode())) {
            mMake.setText(car.getMake());
            mModel.setText(car.getModel());
            mYear.setText(car.getYear());
            mVinText.setText(car.getVin());
        }

        getSupportLoaderManager().destroyLoader(1);

    }

    @Override
    public void onLoaderReset(Loader<Car> loader) {

    }
}
