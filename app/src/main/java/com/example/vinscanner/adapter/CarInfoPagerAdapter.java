package com.example.vinscanner.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.vinscanner.car.Car;
import com.example.vinscanner.ui.CarComplaintFragment;
import com.example.vinscanner.ui.CarInfoFragment;
import com.example.vinscanner.ui.CarRecallFragment;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarInfoPagerAdapter extends FragmentPagerAdapter {

    private Car mCar;
    private String tabTitles[] = new String[] { "General", "Recalls", "Complaints"};

    public CarInfoPagerAdapter(FragmentManager manager){
        super(manager);

    }

    public void setCar(Car car) {
        this.mCar = car;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CarInfoFragment.createFragment(mCar);
            case 1:
                return CarRecallFragment.createFragment(mCar);
            default:
                return CarComplaintFragment.createFragment(mCar);

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
