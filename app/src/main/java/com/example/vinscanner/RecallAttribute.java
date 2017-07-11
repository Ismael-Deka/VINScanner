package com.example.vinscanner;

/**
 * Created by Ismael on 4/27/2017.
 */

public class RecallAttribute {
    private String mComponent;
    private String mSummary;
    private String mConsequence;
    private String mRemedy;

    public RecallAttribute(String component, String summary, String consequence, String remedy){
        mComponent = component;
        mSummary = summary;
        mConsequence = consequence;
        mRemedy = remedy;
    }

    public String getComponent() {
        return mComponent;
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
