package com.ismaelDeka.vinscanner.car;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Created by Ismael on 4/27/2017.
 */

public class RecallAttribute {
    private final String mCampaignNumber;
    private final String mComponent;
    private final String mSummary;
    private final String mConsequence;
    private final String mRemedy;
    private final String mDate;

    public RecallAttribute(String campaign,String component, String summary, String consequence, String remedy, String date){
        mCampaignNumber =campaign;
        mComponent = component;
        mSummary = summary;
        mConsequence = consequence;
        mRemedy = remedy;
        mDate = formatDate(date);
    }

    public static String formatDate(String rawDate){
        if(rawDate != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf_raw = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            try {
                date = sdf_raw.parse(rawDate);
            } catch (ParseException e) {
                Log.e("RecallAttribute", Objects.requireNonNull(e.getLocalizedMessage()));
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
            assert date != null;
            return sdf.format(date);
        }else {
            return null;
        }

    }

    public String getComponent() {
        return mComponent;
    }

    public String getCampaignNumber() {
        return mCampaignNumber;
    }

    public String getDate() {
        return mDate;
    }

    public String getSummary(){
        return mSummary;
    }
    public String getConsequence(){
        return mConsequence;
    }
    public String getRemedy(){
        return  mRemedy;
    }
}
