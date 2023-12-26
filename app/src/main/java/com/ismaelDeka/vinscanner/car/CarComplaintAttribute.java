package com.ismaelDeka.vinscanner.car;

/**
 * Created by Ismael on 11/11/2017.
 */

public class CarComplaintAttribute {

    private final String mODINumber;
    private final String mCrash;
    private final String mFire;
    private final int mNumberInjured;
    private final int mNumberDeaths;
    private final String mDateIncident;
    private final String mDateFiled;
    private final String mComponent;
    private final String mSummary;

    public CarComplaintAttribute(String odiNumber, String crash, String fire, int numberInjured,
                                 int numberDeaths, String dateIncident, String dateFiled, String component, String summary){
        mODINumber = odiNumber;
        mCrash = crash;
        mFire = fire;
        mNumberInjured = numberInjured;
        mNumberDeaths = numberDeaths;
        mDateIncident = RecallAttribute.formatDate(dateIncident);
        mDateFiled = RecallAttribute.formatDate(dateFiled);
        mComponent = component;
        mSummary = summary;

    }

    public String getCrash() {
        return mCrash;
    }

    public String getFire() {
        return mFire;
    }

    public int getNumberDeaths() {
        return mNumberDeaths;
    }

    public int getNumberInjured() {
        return mNumberInjured;
    }

    public String getComponent() {
        return mComponent;
    }

    public String getDateFiled() {
        return mDateFiled;
    }

    public String getDateIncident() {
        return mDateIncident;
    }

    public String getODINumber() {
        return mODINumber;
    }

    public String getSummary() {
        return mSummary;
    }

}
