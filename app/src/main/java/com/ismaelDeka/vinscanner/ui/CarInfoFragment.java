package com.ismaelDeka.vinscanner.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ismaelDeka.vinscanner.R;
import com.ismaelDeka.vinscanner.adapter.CarInfoAdapter;
import com.ismaelDeka.vinscanner.car.Car;
import com.ismaelDeka.vinscanner.car.CarAttribute;

import java.util.ArrayList;


public class CarInfoFragment extends Fragment {
    private Car mCar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_car_info, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.info_list);
        CarInfoAdapter infoAdapter;
        if(savedInstanceState == null) {
            infoAdapter = new CarInfoAdapter(mCar.getVin(), mCar.getMarketPrice(), mCar.getAttributes(), getContext());
        }else {
            infoAdapter = readSavedInstance(savedInstanceState);
        }
        recyclerView.setAdapter(infoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("vin",mCar.getVin());
        outState.putString("price",mCar.getMarketPrice());

        ArrayList<CarAttribute> attributes = mCar.getAttributes();
        for(int i = 0; i < attributes.size(); i++){
            outState.putString("key"+i, attributes.get(i).getKey());
            outState.putString("value"+i,attributes.get(i).getValue());
            outState.putString("category"+i,attributes.get(i).getCategory());
        }
        outState.putInt("attrSize", attributes.size());
    }

    private void setCar(Car newCar) {
        mCar = newCar;
    }

    private CarInfoAdapter readSavedInstance(Bundle b){
        String vin = b.getString("vin");
        String price = b.getString("price");
        int size = b.getInt("attrSize");
        ArrayList<CarAttribute> attributes = new ArrayList<>();
        for(int i = 0; i < size; i++){
            attributes.add(new CarAttribute(b.getString("key"+i),b.getString("value"+i),b.getString("category"+i)));
        }

        return new CarInfoAdapter(vin, price, attributes, getContext());

    }

    public static CarInfoFragment createFragment(Car newCar){
        CarInfoFragment carInfoFragment = new CarInfoFragment();
        carInfoFragment.setCar(newCar);
        return carInfoFragment;
    }

}
