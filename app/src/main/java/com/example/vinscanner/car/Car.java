package com.example.vinscanner.car;

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
    private String mTrim;
    private Bitmap mCarImage[];
    private ArrayList<CarAttribute> mInfo;
    private ArrayList<CarAttribute> mRecallInfo;


    public Car(int newErrorCode, String newMake,String newModel, String newTrim, String newYear, String newVin,Bitmap[] newImage,
               ArrayList<CarAttribute> newAttributes,ArrayList<CarAttribute> newRecallAttribute){

        mErrorCode = newErrorCode;
        if(newMake != null) {
            mMake = newMake.substring(0, 1) + newMake.substring(1).toLowerCase();
        }
        mModel = newModel;
        mTrim = newTrim;
        mYear = newYear;
        mVin = newVin;
        mInfo = newAttributes;
        mCarImage = newImage;
        mRecallInfo = newRecallAttribute;


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

    public String getTrim() {
        return mTrim;
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
        return mInfo;
    }

    public ArrayList<CarAttribute> getRecallInfo() {
        return mRecallInfo;
    }
}
