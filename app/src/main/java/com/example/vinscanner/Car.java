package com.example.vinscanner;

/**
 * Created by Ismael on 2/21/2017.
 */

public class Car {

    private int mErrorCode;
    private final String mVin;
    private String mMake;
    private String mModel;
    private String mYear;

    public Car(int newErrorCode, String newMake,String newModel, String newYear, String newVin){

        mErrorCode = newErrorCode;
        mMake = newMake;
        mModel = newModel;
        mYear = newYear;
        mVin = newVin;


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
}
