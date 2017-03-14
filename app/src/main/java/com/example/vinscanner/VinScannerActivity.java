package com.example.vinscanner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.ByteArrayOutputStream;
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
    private int mRotation = -1;
    private boolean isFlashLightOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vin_scanner);

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

        Toast.makeText(this, "Tap to Focus Camera.", Toast.LENGTH_LONG).show();
    }

    private void rotateFlashButton(float endRotation){
        // Create an animation instance
        Animation an = new RotateAnimation(0.0f, endRotation);

        // Set the animation's parameters
        an.setDuration(1000);               // duration in ms
        an.setRepeatCount(0);                // -1 = infinite repeated
        an.setRepeatMode(Animation.REVERSE); // reverses each repeat
        an.setFillAfter(true);               // keep rotation after animation

        mFlashlight.setAnimation(an);
        mFlashlight.animate();
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
        mRotation = result;

    }

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            doHandleRotation();
            byte[] data = formatFrame(bytes);
            Frame frame = new Frame.Builder().setBitmap(BitmapFactory.decodeByteArray(data,0,data.length)).build();
            detectBarcode(frame);
        }
    };

    private byte[] formatFrame(byte[] bytes){
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);

        return baos.toByteArray();
    }

    @Override
    protected void onPause() {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        turnOffLight(mFlashlight);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(mCamera != null) {
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
        }
        super.onResume();
    }

    private void detectBarcode(Frame frame){
        BarcodeDetector detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.CODE_39).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        if(barcodes.size() > 0) {
            Intent i = new Intent();
            Barcode barcode = barcodes.valueAt(0);
            i.putExtra("barcode", barcode);

            vibrate();

            Log.e(TAG,"Barcode Found.");
            setResult(CommonStatusCodes.SUCCESS, i);
            finish();
        }
    }

    private void startCamera(){
        Log.e(TAG,"Starting Camera.");
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(previewCallback);
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

    private void vibrate(){
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }




}
