package com.example.vinscanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vinscanner.car.Car;
import com.example.vinscanner.car.CarAttribute;

import java.util.ArrayList;

/**
 * Created by Ismael on 4/20/2017.
 */

public class CarRecallFragment extends Fragment {
    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_car_recall, container, false);

        TextView description = (TextView) rootView.findViewById(R.id.description);

        ArrayList<CarAttribute> recallInfo = mCar.getRecallInfo();
        String key;
        String value;

        for(int i = 0; i < recallInfo.size(); i++){
            key = recallInfo.get(i).getKey();
            value = recallInfo.get(i).getValue();
            description.setText(description.getText()+key+": "+value+"\n"+"\n");
        }

        return rootView;
    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    public static CarRecallFragment createFragment(Car newCar){
        CarRecallFragment carRecallFragment = new CarRecallFragment();
        carRecallFragment.setCar(newCar);
        return carRecallFragment;
    }
}
