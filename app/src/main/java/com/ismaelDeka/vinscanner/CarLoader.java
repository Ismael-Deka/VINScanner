package com.ismaelDeka.vinscanner;

import android.app.Activity;
import androidx.loader.content.AsyncTaskLoader;
import android.util.Log;

import com.ismaelDeka.vinscanner.car.Car;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

/**
 * Created by Ismael on 2/21/2017.
 */

public class CarLoader extends AsyncTaskLoader<Car> {

    private String mVin;
    private Activity mParentActivity;

    public CarLoader(Activity activity,String vin) {
        super(activity);
        mVin = vin;
        mParentActivity = activity;

    }

    @Override
    public Car loadInBackground() {
        if(mVin == null || mVin == "")
            return null;
        else {
            updateAndroidSecurityProvider();
            return QueryUtils.extractCar(mVin);
        }
    }

    @SuppressWarnings("deprecation")
    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(getContext());
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), mParentActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }
}
