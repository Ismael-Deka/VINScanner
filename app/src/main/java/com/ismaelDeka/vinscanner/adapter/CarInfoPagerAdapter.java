package com.ismaelDeka.vinscanner.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.ui.CarComplaintFragment;
import com.ismaelDeka.vinscanner.ui.CarInfoFragment;
import com.ismaelDeka.vinscanner.ui.CarRecallFragment;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarInfoPagerAdapter extends FragmentPagerAdapter {

    private Car mCar;
    private int mTotalFragments = 3;
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
        int recallNum = mCar.getRecallInfo().size();
        int complaintNum = mCar.getComplaints().size();

        if(recallNum == 0 && complaintNum == 0) {
            return 1;
        }else if(recallNum == 0 || complaintNum == 0){
            return 2;
        }else{
            return 3;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
