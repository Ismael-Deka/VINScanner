package com.example.vinscanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();




    private EditText mEditText;
    private Button mButton;
    private Button mScanButton;
    private Toolbar toolbar;

    private String mVin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.edit_text);
        mButton = (Button) findViewById(R.id.button);
        mScanButton =(Button) findViewById(R.id.scan_button);

        mEditText.setText("JTNBE46K373015722");


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newVin = mEditText.getText().toString();
                mVin = newVin;
                startCarActivity();

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
