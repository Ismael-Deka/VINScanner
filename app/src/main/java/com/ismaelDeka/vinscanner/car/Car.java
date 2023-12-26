package com.ismaelDeka.vinscanner.car;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Ismael on 2/21/2017.
 */

public class Car {

    private String mErrorCode;
    private String mVin;
    private String mMake;
    private String mModel;
    private String mYear;
    private String mTrim;
    private String mMarketPrice;
    private Bitmap mLogo;
    private ArrayList<CarAttribute> mInfo;
    private ArrayList<RecallAttribute> mRecallInfo;
    private ArrayList<CarComplaintAttribute> mComplaints;


    public Car(){

    }

    public Car(String vin){
        mVin = vin;
        mModel = null;
        mRecallInfo = null;
        mComplaints = null;
    }

    public Car (String newErrorCode, String newMake,String newModel, String newTrim, String newYear,ArrayList<CarAttribute> newAttributes){
        mErrorCode = newErrorCode;
        if(newMake != null) {
            mMake = newMake.charAt(0) + newMake.substring(1).toLowerCase();
        }
        mModel = newModel;
        mTrim = newTrim;
        mYear = newYear;
        mInfo = newAttributes;

    }


    public Car(String newErrorCode, String newMake,String newModel, String newTrim, String newYear, String newVin,
               ArrayList<CarAttribute> newAttributes,ArrayList<RecallAttribute> newRecallAttribute,
               ArrayList<CarComplaintAttribute> newComplaints, Bitmap newLogo,String newMarketPrice){

        mErrorCode = newErrorCode;
        if(newMake != null) {
            mMake = newMake.charAt(0) + newMake.substring(1).toLowerCase();
        }
        mModel = newModel;
        mTrim = newTrim;
        mYear = newYear;
        mLogo = newLogo;
        mVin = newVin;
        mInfo = newAttributes;
        mRecallInfo = newRecallAttribute;
        mMarketPrice = newMarketPrice;
        mComplaints = newComplaints;


    }

    public boolean isCarInfoAvailable(){
        return mModel != null && mRecallInfo != null && mComplaints != null;
    }


    public String getMarketPrice() {
        return mMarketPrice;
    }

    public String getErrorCode() {
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

    public ArrayList<CarAttribute> getAttributes() {
        return mInfo;
    }

    public ArrayList<RecallAttribute> getRecallInfo() {
        return mRecallInfo;
    }

    public Bitmap getLogo() {
        return mLogo;
    }

    public ArrayList<CarComplaintAttribute> getComplaints() {
        return mComplaints;
    }
}
