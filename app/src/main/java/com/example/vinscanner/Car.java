package com.example.vinscanner;

import android.graphics.Bitmap;

/**
 * Created by Ismael on 2/21/2017.
 */

public class Car {

    private int mErrorCode;
    private final String mVin;
    private String mMake;
    private String mModel;
    private String mYear;
    private String mTrim;
    private Bitmap mCarImage;

    public Car(int newErrorCode, String newMake,String newModel, String newYear, String newVin,String newTrim,Bitmap newImage){

        mErrorCode = newErrorCode;
        mMake = newMake.substring(0,1)+newMake.substring(1).toLowerCase();
        mModel = newModel;
        mYear = newYear;
        mVin = newVin;
        mTrim = newTrim;
        mCarImage = newImage;


    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getMake(){

        return mMake;
    }

    public String getModel(){

        return mModel;
    }

    public String getYear(){

        return mYear;
    }

    public String getVin() {
        return mVin;
    }

    public Bitmap getCarImage() {
        return mCarImage;
    }
}
