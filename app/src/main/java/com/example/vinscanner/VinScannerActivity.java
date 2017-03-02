package com.example.vinscanner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.ImageView;
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
    private SurfaceView preview;
    private Camera mCamera;
    private  SurfaceHolder holder;
    private boolean isFlashLightOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_vin_scanner);

        preview = (SurfaceView) findViewById(R.id.camera_preview);
        holder = preview.getHolder();
        holder.addCallback(callBack);

        ImageView backButton = (ImageView) findViewById(R.id.back_button);
        final ImageView flashLightCircle = (ImageView)findViewById(R.id.flashlight_circle);
        final ImageView flashLightIcon = (ImageView) findViewById(R.id.flashlight_icon);
        final GradientDrawable flashCircleImage = (GradientDrawable) getResources().getDrawable(R.drawable.flashlight_circle);

        if(!isFlashLightOn){
            turnOffLight(flashLightCircle,flashLightIcon,flashCircleImage);
        }else{
            turnOnLight(flashLightCircle,flashLightIcon,flashCircleImage);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(VinScannerActivity.this.getParentActivityIntent());
            }
        });

        flashLightCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isFlashLightOn){
                    turnOffLight(flashLightCircle,flashLightIcon,flashCircleImage);
                }else{
                    turnOnLight(flashLightCircle,flashLightIcon,flashCircleImage);
                }

            }
        });


        preview.setOnTouchListener(new View.OnTouchListener() {
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

    private void turnOnLight(ImageView circle, ImageView lightBulb,GradientDrawable flashCircle){

        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
        }

        flashCircle.setColor(getResources().getColor(R.color.flashlight_on));

        circle.setBackground(flashCircle);
        lightBulb.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
        isFlashLightOn = true;

    }

    private void turnOffLight(ImageView circle, ImageView lightBulb,GradientDrawable flashCircle){

        if(mCamera != null){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
        }

        flashCircle.setColor(getResources().getColor(R.color.flashlight_off));


        circle.setBackground(flashCircle);
        lightBulb.setImageResource(R.drawable.ic_lightbulb_outline_white_24dp);
        isFlashLightOn = false;

    }


    private SurfaceHolder.Callback callBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            startCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            int rotation = VinScannerActivity.this.getWindowManager().getDefaultDisplay().getRotation();
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


            result = (info.orientation - degrees + 360) % 360;

            mCamera.setDisplayOrientation(result);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.e(TAG,"Releasing camera");
            mCamera.setPreviewCallback(null);
            mCamera.release();

        }
    };

    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
            byte[] jdata = baos.toByteArray();


            Frame frame = new Frame.Builder().setBitmap(BitmapFactory.decodeByteArray(jdata,0,jdata.length)).build();
            detectBarcode(frame);
        }
    };

    @Override
    protected void onPause() {
        if(mCamera != null) {
            Log.e(TAG, "onPause");
            mCamera.stopPreview();
        }

        super.onPause();
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
            mCamera.setPreviewDisplay(holder);
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
                touchRect.left * 2000 / preview.getWidth() - 1000,
                touchRect.top * 2000 / preview.getHeight() - 1000,
                touchRect.right * 2000 / preview.getWidth() - 1000,
                touchRect.bottom * 2000 / preview.getHeight() - 1000);


    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }




}
