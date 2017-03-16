package com.example.vinscanner;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class VinScannerActivity extends AppCompatActivity {

    public final String TAG = "VinScannerActivity";
    private SurfaceView mPreview;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private ImageButton mFlashlight;
    private VinScanner mScanner;
    private int mOrientation = -1;
    private boolean isFlashLightOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vin_scanner);

        mScanner = new VinScanner(this);

        mPreview = (SurfaceView) findViewById(R.id.camera_preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(callBack);

        ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        mFlashlight = (ImageButton) findViewById(R.id.flashlight);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(VinScannerActivity.this.getParentActivityIntent());
            }
        });


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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        doHandleRotation();
        super.onConfigurationChanged(newConfig);
    }

    private void turnOnLight(ImageButton flashlight){

        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
        }

        flashlight.setImageResource(R.drawable.flashlight_button_on);
        isFlashLightOn = true;

    }

    private void turnOffLight(ImageButton flashlight){

        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
        }

        flashlight.setImageResource(R.drawable.flashlight_button_off);
        isFlashLightOn = false;

    }


    private SurfaceHolder.Callback callBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            startCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            doHandleRotation();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.e(TAG,"Releasing camera");
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();

        }
    };

    private void doHandleRotation(){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
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
        result = (info.orientation-degrees+360)%360;
        mCamera.setDisplayOrientation(result);


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
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(mScanner);
        mCamera.startPreview();
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
            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
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



}
