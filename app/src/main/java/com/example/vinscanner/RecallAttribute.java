package com.example.vinscanner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ismael on 4/27/2017.
 */

public class RecallAttribute {
    private String mCampaignNumber;
    private String mComponent;
    private String mSummary;
    private String mConsequence;
    private String mRemedy;
    private String mDate;

    public RecallAttribute(String campaign,String component, String summary, String consequence, String remedy, String date){
        mCampaignNumber =campaign;
        mComponent = component;
        mSummary = summary;
        mConsequence = consequence;
        mRemedy = remedy;
        mDate = formatDate(date);
    }

    private String formatDate(String rawDate){
        String[] strs = rawDate.split("[(]");
        String str = strs[1];
        String unixDate = str.substring(0,str.length()-7);
        long temp = Long.parseLong(unixDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        unixDate = sdf.format(new Date(temp));
        return unixDate;

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
