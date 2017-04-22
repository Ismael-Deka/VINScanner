package com.example.vinscanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.vinscanner.car.Car;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarInfoPagerAdapter extends FragmentPagerAdapter {

    private Car mCar;
    private String tabTitles[] = new String[] { "General", "Recall"};

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
            default:
                return CarRecallFragment.createFragment(mCar);

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
