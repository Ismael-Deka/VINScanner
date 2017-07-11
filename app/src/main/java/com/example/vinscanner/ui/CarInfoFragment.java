package com.example.vinscanner.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vinscanner.R;
import com.example.vinscanner.car.Car;
import com.example.vinscanner.car.CarAttribute;

import java.util.ArrayList;


public class CarInfoFragment extends Fragment {

    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_car_info, container, false);

        TextView vin = (TextView) rootView.findViewById(R.id.vin_number);
        TextView description = (TextView) rootView.findViewById(R.id.description);

        vin.setText(mCar.getVin());

        ArrayList<CarAttribute> attributes = mCar.getAttributes();
        String key;
        String value;

        for(int i = 3; i < attributes.size(); i++){
            key = attributes.get(i).getKey();
            value = attributes.get(i).getValue();
            description.setText(description.getText()+key+": "+value+"\n"+"\n");
        }

        return rootView;
    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    public static CarInfoFragment createFragment(Car newCar){
        CarInfoFragment carInfoFragment = new CarInfoFragment();
        carInfoFragment.setCar(newCar);
        return carInfoFragment;
    }
}
