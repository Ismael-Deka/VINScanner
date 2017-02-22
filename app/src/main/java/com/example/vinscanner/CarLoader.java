package com.example.vinscanner;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Ismael on 2/21/2017.
 */

public class CarLoader extends AsyncTaskLoader<Car> {

    private String mVin;

    public CarLoader(Context context,String vin) {
        super(context);
        mVin = vin;

    }

    @Override
    public Car loadInBackground() {
        if(mVin == null || mVin == "")
            return null;
        else
         return QueryUtils.extractCar(mVin);
    }
}
