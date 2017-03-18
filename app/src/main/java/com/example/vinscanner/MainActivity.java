package com.example.vinscanner;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();




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

        mEditText.setText("JTNBE46K373015722");


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVin = mEditText.getText().toString();
                startCarActivity();

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
                startCarActivity();
            }
        }
    }

    private void startCarActivity(){

        if(mVin != null){
            Intent carIntent = new Intent(MainActivity.this,CarActivity.class);
            carIntent.putExtra("Vin",mVin);
            startActivity(carIntent);
        }

    }


}
