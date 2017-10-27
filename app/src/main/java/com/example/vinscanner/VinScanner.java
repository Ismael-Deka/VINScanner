package com.example.vinscanner;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.vinscanner.ui.VinScannerActivity;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.ByteArrayOutputStream;

import static android.content.Context.VIBRATOR_SERVICE;
import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by Ismael on 3/16/2017.
 */

@SuppressWarnings("deprecation")
public class VinScanner implements Camera.PreviewCallback {
    private VinScannerActivity mParentActivity;
    private boolean mIsVibrateOnce = false;

    public VinScanner(VinScannerActivity newParentActivity){
        mParentActivity = newParentActivity;
    }
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        byte[] data = formatFrame(bytes,camera);
        Frame frame = new Frame.Builder().setBitmap(BitmapFactory.decodeByteArray(data,0,data.length)).build();
        detectBarcode(frame);
    }
    private byte[] formatFrame(byte[] bytes, Camera camera){
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);

        return baos.toByteArray();

    }
    private void detectBarcode(Frame frame){
        BarcodeDetector detector = new BarcodeDetector.Builder(mParentActivity).setBarcodeFormats(Barcode.CODE_39).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        if(barcodes.size() > 0) {
            Intent i = new Intent();
            Barcode barcode = barcodes.valueAt(0);
            if(!barcode.displayValue.contains(" ")) {
                Toast.makeText(mParentActivity,"VIN: "+barcode.displayValue.substring(1),Toast.LENGTH_SHORT).show();
                i.putExtra("barcode", barcode);
                mParentActivity.setBarcodeOutlineFound();
                if (!mIsVibrateOnce)
                    vibrate();


                Log.e(TAG, "Barcode Found.");
                mParentActivity.setResult(CommonStatusCodes.SUCCESS, i);
                mParentActivity.finish();
            }
        }
    }
        private void vibrate(){
            Vibrator vibrator = (Vibrator)mParentActivity.getSystemService(VIBRATOR_SERVICE);
            if(vibrator.hasVibrator()) {
                vibrator.vibrate(500);
                mIsVibrateOnce = true;
            }
        }
}
