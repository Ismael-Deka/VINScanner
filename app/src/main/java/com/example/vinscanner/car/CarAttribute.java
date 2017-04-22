package com.example.vinscanner.car;

/**
 * Created by Ismael on 3/16/2017.
 */

public class CarAttribute {
    private String mKey;
    private String mValue;

    public CarAttribute(String key, String value){
        mKey = key;
        mValue = value;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue(){
        return mValue;
    }
}
