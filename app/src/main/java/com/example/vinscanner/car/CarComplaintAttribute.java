package com.example.vinscanner.car;

/**
 * Created by Ismael on 11/11/2017.
 */

public class CarComplaintAttribute {

    private String mODINumber;
    private boolean mCrash;
    private boolean mFire;
    private int mNumberInjured;
    private int mNumberDeaths;
    private String mDateIncident;
    private String mDateFiled;
    private String mComponent;
    private String mSummary;

    public CarComplaintAttribute(String odiNumber, boolean crash, boolean fire, int numberInjured,
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

    public boolean isCrash() {
        return mCrash;
    }

    public boolean isFire() {
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

    public void setmComponent(String mComponent) {
        this.mComponent = mComponent;
    }

    public void setCrash(boolean mCrash) {
        this.mCrash = mCrash;
    }

    public void setDateFiled(String mDateFiled) {
        this.mDateFiled = mDateFiled;
    }

    public void setDateIncident(String mDateIncident) {
        this.mDateIncident = mDateIncident;
    }

    public void setFire(boolean mFire) {
        this.mFire = mFire;
    }

    public void setNumberDeaths(int mNumberDeaths) {
        this.mNumberDeaths = mNumberDeaths;
    }

    public void setNumberInjured(int mNumberInjured) {
        this.mNumberInjured = mNumberInjured;
    }

    public void setODINumber(String mODINumber) {
        this.mODINumber = mODINumber;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }
}
