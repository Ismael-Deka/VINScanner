package com.ismaelDeka.vinscanner.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.VinScanner;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

@SuppressWarnings("deprecation")
public class VinScannerActivity extends AppCompatActivity implements VinScanner.OnVinFoundListener {

    public final String TAG = "VinScannerActivity";
    private SurfaceView mPreview;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private ImageButton mFlashlight;
    ImageButton mBackButton;
    private VinScanner mScanner;
    ImageView mBarcodeOutline;
    OrientationEventListener mOrientationEventListener;
    private boolean isFlashLightOn = false;
    private int mCurrentRotation = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrientationEventListener= new OrientationEventListener(this,SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                doHandleRotation();
            }
        };
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vin_scanner);

        Toast.makeText(this,"Tap screen to focus.",Toast.LENGTH_LONG).show();

        mScanner = new VinScanner(this);
        mScanner.setOnVinFoundListener(this);
        mBarcodeOutline = (ImageView) findViewById(R.id.barcode_outline);
        mPreview = (SurfaceView) findViewById(R.id.camera_preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(callBack);

        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mFlashlight = (ImageButton) findViewById(R.id.flashlight);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(VinScannerActivity.this.getParentActivityIntent());
            }
        });

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)||hasFlash()){
            mFlashlight.setVisibility(View.GONE);
        }



        mFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFlashLightOn){
                    turnOffLight(mFlashlight);
                }else{
                    turnOnLight(mFlashlight);
                }

            }
        });

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();

                    doTouchFocus(createFocusArea(x,y));

                }
                return true;
            }
        });

    }






    private void turnOnLight(ImageButton flashlight){
        flashlight.setImageResource(R.drawable.flashlight_button_on);
        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
        }


        isFlashLightOn = true;

    }

    private void turnOffLight(ImageButton flashlight){
        flashlight.setImageResource(R.drawable.flashlight_button_off);
        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
        }
        isFlashLightOn = false;

    }

    public boolean hasFlash() {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 &&
                supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }


    private SurfaceHolder.Callback callBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            startCamera();
            if(mOrientationEventListener.canDetectOrientation()) {
                mOrientationEventListener.enable();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.e(TAG,"Releasing camera");
            mOrientationEventListener.disable();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();

        }
    };


    private void doHandleRotation(){
        if (mCamera != null) {
                int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
                if(rotation == mCurrentRotation){
                    return;
                }
                mCurrentRotation = rotation;
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }
                int result;
                Log.e(TAG, info.orientation + "");
                result = (info.orientation - degrees + 360) % 360;
                Log.e(TAG, result + "");
                mCamera.setDisplayOrientation(result);

        }

    }



    @Override
    protected void onPause() {

        turnOffLight(mFlashlight);
        super.onPause();
    }

    private void startCamera(){
        Log.e(TAG,"Starting Camera.");
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            AlertDialog.Builder messageBox = new AlertDialog.Builder(VinScannerActivity.this);
            messageBox.setTitle(getResources().getString(R.string.app_name));
            messageBox.setMessage(e.getMessage());
            messageBox.setCancelable(false);
            messageBox.setNeutralButton("OK", null);
            messageBox.show();
            e.printStackTrace();
        }

        mCamera.setPreviewCallback(mScanner);
        mCamera.startPreview();
        mCamera.setDisplayOrientation(90);


    }

    public void doTouchFocus(final Rect tfocusRect) {

        Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback(){

            @Override
            public void onAutoFocus(boolean arg0, Camera arg1) {
                if (arg0){
                    mCamera.cancelAutoFocus();
                }
            }
        };


        try {
            List<Camera.Area> focusList = new ArrayList<>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters param = mCamera.getParameters();
            param.setFocusAreas(focusList);
            param.setMeteringAreas(focusList);
            mCamera.setParameters(param);

            mCamera.autoFocus(myAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Unable to autofocus");
        }
    }

    public void setBarcodeOutlineFound(){
        mBarcodeOutline.setImageResource(R.drawable.barcode_found_outline);
    }

    private Rect createFocusArea(float x, float y){

        Rect touchRect = new Rect(
                (int) (x - 100),
                (int) (y - 100),
                (int) (x + 100),
                (int) (y + 100));


        return new Rect(
                touchRect.left * 2000 / mPreview.getWidth() - 1000,
                touchRect.top * 2000 / mPreview.getHeight() - 1000,
                touchRect.right * 2000 / mPreview.getWidth() - 1000,
                touchRect.bottom * 2000 / mPreview.getHeight() - 1000);


    }


    @Override
    public void onVinFound(Barcode barcode) {
        setBarcodeOutlineFound();
        Intent i = new Intent();
        i.putExtra("barcode", barcode);
        setResult(CommonStatusCodes.SUCCESS, i);
        finish();
    }
}
