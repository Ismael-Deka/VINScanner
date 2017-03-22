package com.example.vinscanner;


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.vinscanner.db.CarContract;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getName();


    private CarCursorAdapter mAdapter;



    private EditText mEditText;

    private String mVin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edit_text);
        Button enter = (Button) findViewById(R.id.enter);
        Button scanButton = (Button) findViewById(R.id.scan_button);
        Button clear = (Button) findViewById(R.id.clear);
        ListView carList = (ListView) findViewById(R.id.car_list);

        mAdapter = new CarCursorAdapter(this,null);

        carList.setAdapter(mAdapter);


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

        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonStatusCodes.SUCCESS) {
            Barcode vinBacode;
            if(data != null) {
                vinBacode = data.getParcelableExtra("barcode");
                mVin = vinBacode.displayValue;
                if(mVin.length()!=17){
                    mVin = vinBacode.displayValue.substring(1);
                }
                startCarActivity(null);
            }
        }
    }

    private void startCarActivity(final Uri uri){
        if(mVin != null){
            Intent carIntent = new Intent(MainActivity.this,CarActivity.class);
            carIntent.putExtra("Vin",mVin);
            carIntent.putExtra("Uri", uri);
            startActivity(carIntent);
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
}
