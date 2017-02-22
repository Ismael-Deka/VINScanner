package com.example.vinscanner;


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

import static com.example.vinscanner.R.id.make;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Car> {

    public static final String LOG_TAG = MainActivity.class.getName();


    private String mVin;

    private TextView mMake;
    private TextView mModel;
    private TextView mYear;
    private TextView mVinText;

    private EditText mEditText;
    private Button mButton;

    private ProgressBar mCircle;


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

        mCircle = (ProgressBar) findViewById(R.id.circle);
        mCircle.setVisibility(View.INVISIBLE);
        

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newVin = mEditText.getText().toString();
                if(validateVin(newVin)){
                    mVin = newVin;
                    getSupportLoaderManager().initLoader(1, null, MainActivity.this).forceLoad();
                }
            }
        });


    }
    private boolean validateVin(String vin){

        if(vin.length() < 17){
            Toast toast = Toast.makeText(this, "Please enter a valid VIN.", Toast.LENGTH_LONG);
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

        mMake.setText(car.getMake());
        mModel.setText(car.getModel());
        mYear.setText(car.getYear());
        mVinText.setText(car.getVin());


    }

    @Override
    public void onLoaderReset(Loader<Car> loader) {

    }
}
