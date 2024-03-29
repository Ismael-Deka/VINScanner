package com.ismaelDeka.vinscanner.car;

/**
 * Created by Ismael on 3/16/2017.
 */

public class CarAttribute {
    private final String mKey;
    private final String mValue;
    private final String mCategory;

    public CarAttribute(String key, String value, String category){
        mKey = key;
        mValue = value;
        mCategory = category;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue(){
        return mValue;
    }

    public String getCategory() {
        return mCategory;
    }
}
