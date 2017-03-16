package com.example.vinscanner;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Ismael on 2/21/2017.
 */

public class Car {

    private int mErrorCode;
    private final String mVin;
    private String mMake;
    private String mModel;
    private String mYear;
    private Bitmap mCarImage[];
    private ArrayList<CarAttribute> mAttributes;

    public Car(int newErrorCode, String newMake,String newModel, String newYear, String newVin,Bitmap[] newImage,ArrayList<CarAttribute> newAttributes){

        mErrorCode = newErrorCode;
        mMake = newMake.substring(0,1)+newMake.substring(1).toLowerCase();
        mModel = newModel;
        mYear = newYear;
        mVin = newVin;
        mAttributes = newAttributes;
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

    public Bitmap[] getCarImages() {
        return mCarImage;
    }

    public ArrayList<CarAttribute> getAttributes() {
        return mAttributes;
    }
}
